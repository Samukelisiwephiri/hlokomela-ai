package za.co.hlokomela.api.service;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import za.co.hlokomela.api.domain.WorkOrderStatus;
import za.co.hlokomela.api.domain.MaintenanceWorkOrder;
import za.co.hlokomela.api.repository.AlertRepository;
import za.co.hlokomela.api.repository.MaintenanceWorkOrderRepository;

/**
 * Periodically checks for overdue work orders and fires SMS + alert notifications.
 */
@Component
public class OverdueWorkOrderScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueWorkOrderScheduler.class);

    private static final List<WorkOrderStatus> DONE_STATUSES =
        List.of(WorkOrderStatus.COMPLETED, WorkOrderStatus.CANCELLED);

    private final MaintenanceWorkOrderRepository workOrders;
    private final AlertRepository alertRepository;
    private final SmsNotificationService sms;

    public OverdueWorkOrderScheduler(MaintenanceWorkOrderRepository workOrders,
                                     AlertRepository alertRepository,
                                     SmsNotificationService sms) {
        this.workOrders = workOrders;
        this.alertRepository = alertRepository;
        this.sms = sms;
    }

    /** Runs every hour to flag any work order past its scheduled time. */
    @Scheduled(fixedDelayString = "PT1H", initialDelayString = "PT5M")
    @Transactional
    public void checkOverdue() {
        List<MaintenanceWorkOrder> overdue = workOrders.findOverdue(DONE_STATUSES, Instant.now());
        if (overdue.isEmpty()) {
            log.debug("Overdue check: no overdue work orders found");
            return;
        }
        log.info("Overdue check: {} overdue work order(s) found", overdue.size());
        for (MaintenanceWorkOrder wo : overdue) {
            String msg = "Work order " + wo.getReference() + " assigned to " + wo.getAssignedTeam()
                + " is OVERDUE (scheduled " + wo.getScheduledFor() + ").";
            alertRepository.save(new za.co.hlokomela.api.domain.Alert(
                wo.getMunicipality(), wo.getIncident(), null,
                "Overdue: " + wo.getReference(), msg,
                za.co.hlokomela.api.domain.RiskLevel.HIGH));
            sms.notifyMunicipality(wo.getMunicipality().getId(), "OVERDUE_WORK_ORDER", msg);
        }
    }
}
