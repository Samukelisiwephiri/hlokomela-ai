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
@Table(name = "sensor_readings", indexes = {
    @Index(name = "idx_reading_pipe_time", columnList = "pipe_id,recordedAt"),
    @Index(name = "idx_reading_device_time", columnList = "deviceId,recordedAt")
})
public class SensorReading extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pipe_id", nullable = false)
    private PipeAsset pipe;

    @Column(nullable = false, length = 80)
    private String deviceId;

    @Column(nullable = false)
    private double flowRate;

    @Column(nullable = false)
    private double pressure;

    @Column(nullable = false)
    private double vibration;

    @Column(nullable = false)
    private Instant recordedAt;

    @Column(nullable = false)
    private Instant receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private double riskScore;

    @Column(nullable = false)
    private boolean anomaly;

    @Column(nullable = false, length = 750)
    private String analysisSummary;

    protected SensorReading() {
    }

    public SensorReading(PipeAsset pipe, String deviceId, double flowRate, double pressure,
                         double vibration, Instant recordedAt, RiskLevel riskLevel, double riskScore,
                         boolean anomaly, String analysisSummary) {
        this.pipe = pipe;
        this.deviceId = deviceId;
        this.flowRate = flowRate;
        this.pressure = pressure;
        this.vibration = vibration;
        this.recordedAt = recordedAt;
        this.receivedAt = Instant.now();
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.anomaly = anomaly;
        this.analysisSummary = analysisSummary;
    }

    public UUID getId() { return id; }
    public PipeAsset getPipe() { return pipe; }
    public String getDeviceId() { return deviceId; }
    public double getFlowRate() { return flowRate; }
    public double getPressure() { return pressure; }
    public double getVibration() { return vibration; }
    public Instant getRecordedAt() { return recordedAt; }
    public Instant getReceivedAt() { return receivedAt; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public double getRiskScore() { return riskScore; }
    public boolean isAnomaly() { return anomaly; }
    public String getAnalysisSummary() { return analysisSummary; }
}
