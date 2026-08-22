package za.co.hlokomela.api.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hlokomela.api.domain.Incident;
import za.co.hlokomela.api.domain.IncidentStatus;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    Page<Incident> findByMunicipalityIdOrderByCreatedAtDesc(UUID municipalityId, Pageable pageable);
    Optional<Incident> findByReferenceIgnoreCase(String reference);
    Optional<Incident> findFirstByPipeIdAndStatusInOrderByCreatedAtDesc(UUID pipeId, Collection<IncidentStatus> statuses);
    long countByMunicipalityIdAndStatusNot(UUID municipalityId, IncidentStatus status);
}
