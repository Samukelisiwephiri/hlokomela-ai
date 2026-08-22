package za.co.hlokomela.api.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import za.co.hlokomela.api.domain.IncidentSource;
import za.co.hlokomela.api.domain.IncidentStatus;
import za.co.hlokomela.api.domain.IncidentType;
import za.co.hlokomela.api.domain.PipeStatus;
import za.co.hlokomela.api.domain.RiskLevel;
import za.co.hlokomela.api.domain.WorkOrderStatus;

public final class OperationsDtos {
    private OperationsDtos() {
    }

    public record CreatePipeRequest(
        @NotBlank @Size(max = 48) String code,
        @Size(max = 80) String deviceId,
        @NotBlank @Size(max = 160) String locationName,
        @Size(max = 80) String ward,
        @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        @DecimalMin("0.1") @DecimalMax("10000.0") double baselineFlowRate,
        @DecimalMin("0.1") @DecimalMax("100.0") double baselinePressure,
        @DecimalMin("0.0") @DecimalMax("100.0") double minimumSafePressure,
        @DecimalMin("0.0") @DecimalMax("100.0") double maximumSafeVibration
    ) { }

    public record PipeResponse(UUID id, String code, String deviceId, String locationName, String ward,
                               Double latitude, Double longitude, PipeStatus status, double baselineFlowRate,
                               double baselinePressure, double minimumSafePressure, double maximumSafeVibration,
                               RiskLevel currentRiskLevel, double currentRiskScore,
                               String latestRecommendation, Instant lastReadingAt) { }

    public record IncidentResponse(UUID id, String reference, String pipeCode, String reportReference,
                                   IncidentSource source, IncidentType type, String title, String description,
                                   RiskLevel riskLevel, double riskScore, double confidence,
                                   String recommendedAction, double estimatedWaterLossLitres,
                                   IncidentStatus status, String assignedTeam, Instant dispatchedAt,
                                   Instant resolvedAt, Instant createdAt, Instant updatedAt) { }

    public record UpdateIncidentStatusRequest(@NotNull IncidentStatus status) { }

    public record DispatchRequest(@NotBlank @Size(max = 120) String assignedTeam,
                                  Instant scheduledFor, @Size(max = 1500) String notes) { }

    public record WorkOrderResponse(UUID id, String reference, String incidentReference, String pipeCode,
                                    String assignedTeam, WorkOrderStatus status, Instant scheduledFor,
                                    Instant completedAt, String notes, Instant createdAt) { }

    public record AlertResponse(UUID id, String incidentReference, String title, String message,
                                RiskLevel riskLevel, Instant readAt, Instant createdAt) { }

    public record DashboardSummaryResponse(int totalPipes, int highRiskPipes, long activeIncidents,
                                           long reportsToday, int sensorsOnline, int sensorCoveragePercent,
                                           List<IncidentResponse> priorityIncidents,
                                           List<AlertResponse> recentAlerts) { }
}
