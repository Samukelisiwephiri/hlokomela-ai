package za.co.hlokomela.api.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hlokomela.api.domain.Alert;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findByMunicipalityIdOrderByCreatedAtDesc(UUID municipalityId, Pageable pageable);
    List<Alert> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId, Pageable pageable);
}
