package za.co.hlokomela.api.service;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.hlokomela.api.domain.Alert;
import za.co.hlokomela.api.domain.CommunityReport;
import za.co.hlokomela.api.domain.Incident;
import za.co.hlokomela.api.domain.IncidentSource;
import za.co.hlokomela.api.domain.IncidentStatus;
import za.co.hlokomela.api.domain.MaintenanceWorkOrder;
import za.co.hlokomela.api.domain.PipeAsset;
import za.co.hlokomela.api.domain.RiskLevel;
import za.co.hlokomela.api.domain.UserAccount;
import za.co.hlokomela.api.domain.WorkOrderStatus;
import za.co.hlokomela.api.exception.ResourceNotFoundException;
import za.co.hlokomela.api.repository.AlertRepository;
import za.co.hlokomela.api.repository.IncidentRepository;
import za.co.hlokomela.api.repository.MaintenanceWorkOrderRepository;
import za.co.hlokomela.api.service.RiskAssessmentService.RiskAssessment;
import za.co.hlokomela.api.web.dto.OperationsDtos.DispatchRequest;

@Service
public class IncidentService {
    private static final List<IncidentStatus> ACTIVE_STATUSES = List.of(
        IncidentStatus.OPEN, IncidentStatus.ACKNOWLEDGED, IncidentStatus.DISPATCHED, IncidentStatus.IN_PROGRESS);

    private final IncidentRepository incidents;
    private final AlertRepository alerts;
    private final MaintenanceWorkOrderRepository workOrders;
    private final CurrentUserService currentUsers;
    private final ReferenceGenerator references;

    public IncidentService(IncidentRepository incidents, AlertRepository alerts,
                           MaintenanceWorkOrderRepository workOrders, CurrentUserService currentUsers,
                           ReferenceGenerator references) {
        this.incidents = incidents;
        this.alerts = alerts;
        this.workOrders = workOrders;
        this.currentUsers = currentUsers;
        this.references = references;
    }

    @Transactional
    public Incident createOrUpdateFromTelemetry(PipeAsset pipe, RiskAssessment assessment) {
        if (assessment.riskLevel() == RiskLevel.LOW) {
            return null;
        }
        Incident existing = incidents.findFirstByPipeIdAndStatusInOrderByCreatedAtDesc(pipe.getId(), ACTIVE_STATUSES)
            .orElse(null);
        if (existing != null) {
            boolean escalated = assessment.riskLevel().ordinal() > existing.getRiskLevel().ordinal();
            if (assessment.score() >= existing.getRiskScore()) {
                existing.setRiskScore(assessment.score());
                existing.setRiskLevel(assessment.riskLevel());
                existing.setConfidence(assessment.confidence());
                existing.setRecommendedAction(assessment.recommendedAction());
                existing.setEstimatedWaterLossLitres(assessment.estimatedWaterLossLitres());
            }
            Incident saved = incidents.save(existing);
            if (escalated) {
                createAlert(saved, "Risk escalated for " + pipe.getCode(), assessment.summary());
            }
            return saved;
        }

        Incident incident = new Incident(references.next("INC"), pipe.getMunicipality(), pipe, null,
            IncidentSource.SENSOR, assessment.incidentType(), "Sensor anomaly at " + pipe.getCode(),
            assessment.summary(), assessment.riskLevel(), assessment.score(), assessment.confidence(),
            assessment.recommendedAction(), assessment.estimatedWaterLossLitres());
        Incident saved = incidents.save(incident);
        createAlert(saved, "New " + assessment.riskLevel().name().toLowerCase() + " risk at " + pipe.getCode(),
            assessment.recommendedAction());
        return saved;
    }

    @Transactional
    public Incident createFromCommunityReport(CommunityReport report, RiskAssessment assessment) {
        String pipeText = report.getPipe() == null ? report.getLocation() : report.getPipe().getCode();
        Incident incident = new Incident(references.next("INC"), report.getMunicipality(), report.getPipe(), report,
            IncidentSource.COMMUNITY_REPORT, assessment.incidentType(), "Community report at " + pipeText,
            report.getDescription(), assessment.riskLevel(), assessment.score(), assessment.confidence(),
            assessment.recommendedAction(), assessment.estimatedWaterLossLitres());
        Incident saved = incidents.save(incident);
        createAlert(saved, "New community report: " + report.getReference(), assessment.recommendedAction());
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Incident> list(UserAccount account, Pageable pageable) {
        currentUsers.requireOneOf(account, za.co.hlokomela.api.domain.Role.MUNICIPAL_OPERATOR,
            za.co.hlokomela.api.domain.Role.ADMIN);
        return incidents.findByMunicipalityIdOrderByCreatedAtDesc(account.getMunicipality().getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Incident getForMunicipality(UserAccount account, String reference) {
        currentUsers.requireOneOf(account, za.co.hlokomela.api.domain.Role.MUNICIPAL_OPERATOR,
            za.co.hlokomela.api.domain.Role.ADMIN);
        Incident incident = incidents.findByReferenceIgnoreCase(reference)
            .orElseThrow(() -> new ResourceNotFoundException("Incident was not found"));
        if (!incident.getMunicipality().getId().equals(account.getMunicipality().getId())) {
            throw new ResourceNotFoundException("Incident was not found");
        }
        return incident;
    }

    @Transactional
    public Incident updateStatus(UserAccount account, String reference, IncidentStatus status) {
        Incident incident = getForMunicipality(account, reference);
        incident.setStatus(status);
        if (status == IncidentStatus.RESOLVED || status == IncidentStatus.CLOSED) {
            incident.setResolvedAt(Instant.now());
        } else {
            incident.setResolvedAt(null);
        }
        Incident saved = incidents.save(incident);
        createAlert(saved, "Incident " + saved.getReference() + " updated", "Status changed to " + status.name() + ".");
        return saved;
    }

    @Transactional
    public MaintenanceWorkOrder dispatch(UserAccount account, String reference, DispatchRequest request) {
        Incident incident = getForMunicipality(account, reference);
        incident.setAssignedTeam(request.assignedTeam().trim());
        incident.setStatus(IncidentStatus.DISPATCHED);
        incident.setDispatchedAt(Instant.now());
        incidents.save(incident);
        MaintenanceWorkOrder order = new MaintenanceWorkOrder(references.next("WO"), incident.getMunicipality(), incident,
            request.assignedTeam().trim(), request.scheduledFor(), request.notes());
        order.setStatus(WorkOrderStatus.DISPATCHED);
        MaintenanceWorkOrder saved = workOrders.save(order);
        createAlert(incident, "Maintenance team dispatched", request.assignedTeam().trim() + " has been assigned to " + reference + ".");
        return saved;
    }

    @Transactional(readOnly = true)
    public List<MaintenanceWorkOrder> listWorkOrders(UserAccount account) {
        currentUsers.requireOneOf(account, za.co.hlokomela.api.domain.Role.MUNICIPAL_OPERATOR,
            za.co.hlokomela.api.domain.Role.ADMIN);
        return workOrders.findTop20ByMunicipalityIdOrderByCreatedAtDesc(account.getMunicipality().getId());
    }

    private void createAlert(Incident incident, String title, String message) {
        alerts.save(new Alert(incident.getMunicipality(), incident, null, title, message, incident.getRiskLevel()));
    }
}
