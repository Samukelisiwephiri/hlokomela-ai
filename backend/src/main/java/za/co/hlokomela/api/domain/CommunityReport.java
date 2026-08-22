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
@Table(name = "community_reports", indexes = {
    @Index(name = "idx_report_municipality_created", columnList = "municipality_id,createdAt"),
    @Index(name = "idx_report_reporter_created", columnList = "reporter_id,createdAt")
})
public class CommunityReport extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 24)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserAccount reporter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipality_id", nullable = false)
    private Municipality municipality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipe_id")
    private PipeAsset pipe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ReportType type;

    @Column(nullable = false, length = 180)
    private String location;

    private Double latitude;
    private Double longitude;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(length = 320)
    private String photoUrl;

    @Column(length = 120)
    private String photoStorageKey;

    @Column(nullable = false)
    private Instant consentAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private ReportStatus status = ReportStatus.SUBMITTED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private double urgencyScore;

    protected CommunityReport() {
    }

    public CommunityReport(String reference, UserAccount reporter, Municipality municipality, PipeAsset pipe,
                           ReportType type, String location, Double latitude, Double longitude,
                           String description, String photoUrl, String photoStorageKey, Instant consentAt, RiskLevel riskLevel,
                           double urgencyScore) {
        this.reference = reference;
        this.reporter = reporter;
        this.municipality = municipality;
        this.pipe = pipe;
        this.type = type;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.photoUrl = photoUrl;
        this.photoStorageKey = photoStorageKey;
        this.consentAt = consentAt;
        this.riskLevel = riskLevel;
        this.urgencyScore = urgencyScore;
    }

    public UUID getId() { return id; }
    public String getReference() { return reference; }
    public UserAccount getReporter() { return reporter; }
    public Municipality getMunicipality() { return municipality; }
    public PipeAsset getPipe() { return pipe; }
    public ReportType getType() { return type; }
    public String getLocation() { return location; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getDescription() { return description; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getPhotoStorageKey() { return photoStorageKey; }
    public void setPhotoStorageKey(String photoStorageKey) { this.photoStorageKey = photoStorageKey; }
    public Instant getConsentAt() { return consentAt; }
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public double getUrgencyScore() { return urgencyScore; }
    public void setUrgencyScore(double urgencyScore) { this.urgencyScore = urgencyScore; }
}
