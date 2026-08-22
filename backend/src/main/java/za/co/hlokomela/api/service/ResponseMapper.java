package za.co.hlokomela.api.service;

import za.co.hlokomela.api.domain.Alert;
import za.co.hlokomela.api.domain.CommunityReport;
import za.co.hlokomela.api.domain.Incident;
import za.co.hlokomela.api.domain.MaintenanceWorkOrder;
import za.co.hlokomela.api.domain.PipeAsset;
import za.co.hlokomela.api.domain.SensorReading;
import za.co.hlokomela.api.domain.UserAccount;
import za.co.hlokomela.api.web.dto.AuthDtos.UserResponse;
import za.co.hlokomela.api.web.dto.OperationsDtos.AlertResponse;
import za.co.hlokomela.api.web.dto.OperationsDtos.IncidentResponse;
import za.co.hlokomela.api.web.dto.OperationsDtos.PipeResponse;
import za.co.hlokomela.api.web.dto.OperationsDtos.WorkOrderResponse;
import za.co.hlokomela.api.web.dto.ReportDtos.ReportResponse;
import za.co.hlokomela.api.web.dto.TelemetryDtos.ReadingResponse;

public final class ResponseMapper {
    private ResponseMapper() {
    }

    public static UserResponse user(UserAccount user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
            user.getPhone(), user.getRole(), user.getMunicipality().getCode(), user.getMunicipality().getName());
    }

    public static PipeResponse pipe(PipeAsset pipe) {
        return new PipeResponse(pipe.getId(), pipe.getCode(), pipe.getDeviceId(), pipe.getLocationName(), pipe.getWard(),
            pipe.getLatitude(), pipe.getLongitude(), pipe.getStatus(), pipe.getBaselineFlowRate(),
            pipe.getBaselinePressure(), pipe.getMinimumSafePressure(), pipe.getMaximumSafeVibration(),
            pipe.getCurrentRiskLevel(), pipe.getCurrentRiskScore(), pipe.getLatestRecommendation(), pipe.getLastReadingAt());
    }

    public static ReadingResponse reading(SensorReading reading, String incidentReference) {
        return new ReadingResponse(reading.getId(), reading.getPipe().getCode(), reading.getDeviceId(),
            reading.getFlowRate(), reading.getPressure(), reading.getVibration(), reading.getRecordedAt(),
            reading.getRiskLevel(), reading.getRiskScore(), reading.isAnomaly(), reading.getAnalysisSummary(), incidentReference);
    }

    public static ReportResponse report(CommunityReport report) {
        return new ReportResponse(report.getId(), report.getReference(), report.getType(), report.getLocation(),
            report.getLatitude(), report.getLongitude(), report.getDescription(), report.getPhotoUrl(),
            report.getPipe() == null ? null : report.getPipe().getCode(), report.getStatus(), report.getRiskLevel(),
            report.getUrgencyScore(), report.getCreatedAt(), report.getUpdatedAt());
    }

    public static IncidentResponse incident(Incident incident) {
        return new IncidentResponse(incident.getId(), incident.getReference(),
            incident.getPipe() == null ? null : incident.getPipe().getCode(),
            incident.getCommunityReport() == null ? null : incident.getCommunityReport().getReference(),
            incident.getSource(), incident.getType(), incident.getTitle(), incident.getDescription(),
            incident.getRiskLevel(), incident.getRiskScore(), incident.getConfidence(), incident.getRecommendedAction(),
            incident.getEstimatedWaterLossLitres(), incident.getStatus(), incident.getAssignedTeam(),
            incident.getDispatchedAt(), incident.getResolvedAt(), incident.getCreatedAt(), incident.getUpdatedAt());
    }

    public static AlertResponse alert(Alert alert) {
        return new AlertResponse(alert.getId(), alert.getIncident() == null ? null : alert.getIncident().getReference(),
            alert.getTitle(), alert.getMessage(), alert.getRiskLevel(), alert.getReadAt(), alert.getCreatedAt());
    }

    public static WorkOrderResponse workOrder(MaintenanceWorkOrder order) {
        Incident incident = order.getIncident();
        return new WorkOrderResponse(order.getId(), order.getReference(), incident.getReference(),
            incident.getPipe() == null ? null : incident.getPipe().getCode(), order.getAssignedTeam(), order.getStatus(),
            order.getScheduledFor(), order.getCompletedAt(), order.getNotes(), order.getCreatedAt());
    }
}
