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
@Table(name = "alerts", indexes = @Index(name = "idx_alert_municipality_created", columnList = "municipality_id,createdAt"))
public class Alert extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipality_id", nullable = false)
    private Municipality municipality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident incident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private UserAccount recipient;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private RiskLevel riskLevel;

    private Instant readAt;

    protected Alert() {
    }

    public Alert(Municipality municipality, Incident incident, UserAccount recipient,
                 String title, String message, RiskLevel riskLevel) {
        this.municipality = municipality;
        this.incident = incident;
        this.recipient = recipient;
        this.title = title;
        this.message = message;
        this.riskLevel = riskLevel;
    }

    public UUID getId() { return id; }
    public Municipality getMunicipality() { return municipality; }
    public Incident getIncident() { return incident; }
    public UserAccount getRecipient() { return recipient; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public Instant getReadAt() { return readAt; }
    public void markRead() { this.readAt = Instant.now(); }
}
