package za.co.hlokomela.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pipe_assets", indexes = {
    @Index(name = "idx_pipe_municipality", columnList = "municipality_id"),
    @Index(name = "idx_pipe_device_id", columnList = "deviceId")
})
public class PipeAsset extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 48)
    private String code;

    @Column(unique = true, length = 80)
    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipality_id", nullable = false)
    private Municipality municipality;

    @Column(nullable = false, length = 160)
    private String locationName;

    @Column(length = 80)
    private String ward;

    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    private double baselineFlowRate;

    @Column(nullable = false)
    private double baselinePressure;

    @Column(nullable = false)
    private double minimumSafePressure;

    @Column(nullable = false)
    private double maximumSafeVibration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PipeStatus status = PipeStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private RiskLevel currentRiskLevel = RiskLevel.LOW;

    @Column(nullable = false)
    private double currentRiskScore;

    @Column(length = 500)
    private String latestRecommendation;

    private Instant lastReadingAt;

    protected PipeAsset() {
    }

    public PipeAsset(String code, String deviceId, Municipality municipality, String locationName,
                     String ward, Double latitude, Double longitude, double baselineFlowRate,
                     double baselinePressure, double minimumSafePressure, double maximumSafeVibration) {
        this.code = code;
        this.deviceId = deviceId;
        this.municipality = municipality;
        this.locationName = locationName;
        this.ward = ward;
        this.latitude = latitude;
        this.longitude = longitude;
        this.baselineFlowRate = baselineFlowRate;
        this.baselinePressure = baselinePressure;
        this.minimumSafePressure = minimumSafePressure;
        this.maximumSafeVibration = maximumSafeVibration;
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public Municipality getMunicipality() { return municipality; }
    public void setMunicipality(Municipality municipality) { this.municipality = municipality; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public double getBaselineFlowRate() { return baselineFlowRate; }
    public void setBaselineFlowRate(double baselineFlowRate) { this.baselineFlowRate = baselineFlowRate; }
    public double getBaselinePressure() { return baselinePressure; }
    public void setBaselinePressure(double baselinePressure) { this.baselinePressure = baselinePressure; }
    public double getMinimumSafePressure() { return minimumSafePressure; }
    public void setMinimumSafePressure(double minimumSafePressure) { this.minimumSafePressure = minimumSafePressure; }
    public double getMaximumSafeVibration() { return maximumSafeVibration; }
    public void setMaximumSafeVibration(double maximumSafeVibration) { this.maximumSafeVibration = maximumSafeVibration; }
    public PipeStatus getStatus() { return status; }
    public void setStatus(PipeStatus status) { this.status = status; }
    public RiskLevel getCurrentRiskLevel() { return currentRiskLevel; }
    public void setCurrentRiskLevel(RiskLevel currentRiskLevel) { this.currentRiskLevel = currentRiskLevel; }
    public double getCurrentRiskScore() { return currentRiskScore; }
    public void setCurrentRiskScore(double currentRiskScore) { this.currentRiskScore = currentRiskScore; }
    public String getLatestRecommendation() { return latestRecommendation; }
    public void setLatestRecommendation(String latestRecommendation) { this.latestRecommendation = latestRecommendation; }
    public Instant getLastReadingAt() { return lastReadingAt; }
    public void setLastReadingAt(Instant lastReadingAt) { this.lastReadingAt = lastReadingAt; }
}
