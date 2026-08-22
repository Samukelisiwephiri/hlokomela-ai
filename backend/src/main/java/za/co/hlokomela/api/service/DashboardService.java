package za.co.hlokomela.api.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.hlokomela.api.domain.Incident;
import za.co.hlokomela.api.domain.IncidentStatus;
import za.co.hlokomela.api.domain.PipeAsset;
import za.co.hlokomela.api.domain.RiskLevel;
import za.co.hlokomela.api.domain.UserAccount;
import za.co.hlokomela.api.repository.AlertRepository;
import za.co.hlokomela.api.repository.CommunityReportRepository;
import za.co.hlokomela.api.repository.IncidentRepository;
import za.co.hlokomela.api.repository.PipeAssetRepository;
import za.co.hlokomela.api.web.dto.OperationsDtos.DashboardSummaryResponse;

@Service
public class DashboardService {
    private static final List<IncidentStatus> ACTIVE_STATUSES = List.of(
        IncidentStatus.OPEN, IncidentStatus.ACKNOWLEDGED, IncidentStatus.DISPATCHED, IncidentStatus.IN_PROGRESS);
    private final PipeAssetRepository pipes;
    private final IncidentRepository incidents;
    private final CommunityReportRepository reports;
    private final AlertRepository alerts;
    private final CurrentUserService currentUsers;

    public DashboardService(PipeAssetRepository pipes, IncidentRepository incidents,
                            CommunityReportRepository reports, AlertRepository alerts, CurrentUserService currentUsers) {
        this.pipes = pipes;
        this.incidents = incidents;
        this.reports = reports;
        this.alerts = alerts;
        this.currentUsers = currentUsers;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary(UserAccount account) {
        currentUsers.requireOneOf(account, za.co.hlokomela.api.domain.Role.MUNICIPAL_OPERATOR,
            za.co.hlokomela.api.domain.Role.ADMIN);
        var municipalityId = account.getMunicipality().getId();
        List<PipeAsset> allPipes = pipes.findByMunicipalityIdOrderByCodeAsc(municipalityId);
        int highRiskPipes = (int) allPipes.stream()
            .filter(pipe -> pipe.getCurrentRiskLevel().ordinal() >= RiskLevel.HIGH.ordinal()).count();
        Instant onlineThreshold = Instant.now().minus(30, ChronoUnit.MINUTES);
        int online = (int) allPipes.stream().filter(pipe -> pipe.getLastReadingAt() != null
            && !pipe.getLastReadingAt().isBefore(onlineThreshold)).count();
        int coverage = allPipes.isEmpty() ? 0 : (int) Math.round(online * 100.0 / allPipes.size());
        List<Incident> priority = incidents.findByMunicipalityIdOrderByCreatedAtDesc(municipalityId, PageRequest.of(0, 20))
            .getContent().stream().filter(incident -> ACTIVE_STATUSES.contains(incident.getStatus()))
            .sorted(Comparator.comparingDouble(Incident::getRiskScore).reversed()).limit(5).toList();
        return new DashboardSummaryResponse(allPipes.size(), highRiskPipes,
            incidents.countByMunicipalityIdAndStatusNot(municipalityId, IncidentStatus.RESOLVED),
            reports.countByMunicipalityIdAndCreatedAtAfter(municipalityId, Instant.now().minus(1, ChronoUnit.DAYS)),
            online, coverage, priority.stream().map(ResponseMapper::incident).toList(),
            alerts.findByMunicipalityIdOrderByCreatedAtDesc(municipalityId, PageRequest.of(0, 8)).stream()
                .map(ResponseMapper::alert).toList());
    }
}
