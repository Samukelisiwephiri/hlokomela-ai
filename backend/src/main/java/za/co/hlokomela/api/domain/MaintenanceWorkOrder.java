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
@Table(name = "maintenance_work_orders", indexes = @Index(name = "idx_workorder_municipality_status", columnList = "municipality_id,status"))
public class MaintenanceWorkOrder extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 24)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipality_id", nullable = false)
    private Municipality municipality;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Column(nullable = false, length = 120)
    private String assignedTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WorkOrderStatus status = WorkOrderStatus.SCHEDULED;

    private Instant scheduledFor;
    private Instant completedAt;

    @Column(length = 1500)
    private String notes;

    protected MaintenanceWorkOrder() {
    }

    public MaintenanceWorkOrder(String reference, Municipality municipality, Incident incident,
                                String assignedTeam, Instant scheduledFor, String notes) {
        this.reference = reference;
        this.municipality = municipality;
        this.incident = incident;
        this.assignedTeam = assignedTeam;
        this.scheduledFor = scheduledFor;
        this.notes = notes;
    }

    public UUID getId() { return id; }
    public String getReference() { return reference; }
    public Municipality getMunicipality() { return municipality; }
    public Incident getIncident() { return incident; }
    public String getAssignedTeam() { return assignedTeam; }
    public void setAssignedTeam(String assignedTeam) { this.assignedTeam = assignedTeam; }
    public WorkOrderStatus getStatus() { return status; }
    public void setStatus(WorkOrderStatus status) { this.status = status; }
    public Instant getScheduledFor() { return scheduledFor; }
    public void setScheduledFor(Instant scheduledFor) { this.scheduledFor = scheduledFor; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
