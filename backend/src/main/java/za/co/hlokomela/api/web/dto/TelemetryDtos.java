package za.co.hlokomela.api.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
import za.co.hlokomela.api.domain.RiskLevel;

public final class TelemetryDtos {
    private TelemetryDtos() {
    }

    public record TelemetryRequest(
        @NotBlank @Size(max = 80) @Pattern(regexp = "^[A-Za-z0-9._:-]+$", message = "contains unsupported characters") String deviceId,
        @NotNull @DecimalMin("0.0") @DecimalMax("10000.0") @JsonAlias("flowRate") Double flow,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double pressure,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double vibration,
        Instant timestamp
    ) { }

    public record ReadingResponse(UUID id, String pipeCode, String deviceId, double flowRate,
                                  double pressure, double vibration, Instant recordedAt,
                                  RiskLevel riskLevel, double riskScore, boolean anomaly,
                                  String analysisSummary, String incidentReference) { }
}
