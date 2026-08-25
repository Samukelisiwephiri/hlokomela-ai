package za.co.hlokomela.api.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.hlokomela.api.domain.MaintenanceWorkOrder;
import za.co.hlokomela.api.domain.WorkOrderStatus;
import java.time.Instant;

public interface MaintenanceWorkOrderRepository extends JpaRepository<MaintenanceWorkOrder, UUID> {
    List<MaintenanceWorkOrder> findTop20ByMunicipalityIdOrderByCreatedAtDesc(UUID municipalityId);

    @Query("SELECT w FROM MaintenanceWorkOrder w WHERE w.status NOT IN :doneStatuses AND w.scheduledFor < :now")
    List<MaintenanceWorkOrder> findOverdue(@Param("doneStatuses") List<WorkOrderStatus> doneStatuses,
                                           @Param("now") Instant now);
}
