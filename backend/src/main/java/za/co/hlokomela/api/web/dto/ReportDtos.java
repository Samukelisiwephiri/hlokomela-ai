package za.co.hlokomela.api.web.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
import za.co.hlokomela.api.domain.ReportStatus;
import za.co.hlokomela.api.domain.ReportType;
import za.co.hlokomela.api.domain.RiskLevel;

public final class ReportDtos {
    private ReportDtos() {
    }

    public record CreateReportRequest(
        @NotNull ReportType type,
        @NotBlank @Size(max = 180) String location,
        @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        @NotBlank @Size(max = 2000) String description,
        @Size(max = 48) String pipeCode,
        @AssertTrue(message = "POPIA consent is required before submitting a report") boolean consentGiven
    ) { }

    public record ReportStatusUpdateRequest(@NotNull ReportStatus status) { }

    public record ReportResponse(UUID id, String reference, ReportType type, String location,
                                 Double latitude, Double longitude, String description, String photoUrl,
                                 String pipeCode, ReportStatus status, RiskLevel riskLevel,
                                 double urgencyScore, Instant submittedAt, Instant updatedAt) { }
}
