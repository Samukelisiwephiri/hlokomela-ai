package za.co.hlokomela.api.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hlokomela.api.domain.SensorReading;

public interface SensorReadingRepository extends JpaRepository<SensorReading, UUID> {
    List<SensorReading> findTop50ByPipeIdOrderByRecordedAtDesc(UUID pipeId);
    Optional<SensorReading> findFirstByPipeIdOrderByRecordedAtDesc(UUID pipeId);
    long countByPipeMunicipalityIdAndRecordedAtAfter(UUID municipalityId, Instant threshold);
}
