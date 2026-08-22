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
@Table(name = "incidents", indexes = {
    @Index(name = "idx_incident_municipality_status", columnList = "municipality_id,status"),
    @Index(name = "idx_incident_pipe_status", columnList = "pipe_id,status")
})
public class Incident extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 24)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipality_id", nullable = false)
    private Municipality municipality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipe_id")
    private PipeAsset pipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_report_id")
    private CommunityReport communityReport;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private IncidentSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private IncidentType type;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private double riskScore;

    @Column(nullable = false)
    private double confidence;

    @Column(nullable = false, length = 750)
    private String recommendedAction;

    @Column(nullable = false)
    private double estimatedWaterLossLitres;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private IncidentStatus status = IncidentStatus.OPEN;

    @Column(length = 120)
    private String assignedTeam;

    private Instant dispatchedAt;
    private Instant resolvedAt;

    protected Incident() {
    }

    public Incident(String reference, Municipality municipality, PipeAsset pipe, CommunityReport communityReport,
                    IncidentSource source, IncidentType type, String title, String description,
                    RiskLevel riskLevel, double riskScore, double confidence, String recommendedAction,
                    double estimatedWaterLossLitres) {
        this.reference = reference;
        this.municipality = municipality;
        this.pipe = pipe;
        this.communityReport = communityReport;
        this.source = source;
        this.type = type;
        this.title = title;
        this.description = description;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.confidence = confidence;
        this.recommendedAction = recommendedAction;
        this.estimatedWaterLossLitres = estimatedWaterLossLitres;
    }

    public UUID getId() { return id; }
    public String getReference() { return reference; }
    public Municipality getMunicipality() { return municipality; }
    public PipeAsset getPipe() { return pipe; }
    public CommunityReport getCommunityReport() { return communityReport; }
    public IncidentSource getSource() { return source; }
    public IncidentType getType() { return type; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
    public double getEstimatedWaterLossLitres() { return estimatedWaterLossLitres; }
    public void setEstimatedWaterLossLitres(double estimatedWaterLossLitres) { this.estimatedWaterLossLitres = estimatedWaterLossLitres; }
    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }
    public String getAssignedTeam() { return assignedTeam; }
    public void setAssignedTeam(String assignedTeam) { this.assignedTeam = assignedTeam; }
    public Instant getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(Instant dispatchedAt) { this.dispatchedAt = dispatchedAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
}
