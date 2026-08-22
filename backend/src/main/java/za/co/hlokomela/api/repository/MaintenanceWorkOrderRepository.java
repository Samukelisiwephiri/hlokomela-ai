package za.co.hlokomela.api.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hlokomela.api.domain.MaintenanceWorkOrder;

public interface MaintenanceWorkOrderRepository extends JpaRepository<MaintenanceWorkOrder, UUID> {
    List<MaintenanceWorkOrder> findTop20ByMunicipalityIdOrderByCreatedAtDesc(UUID municipalityId);
}
