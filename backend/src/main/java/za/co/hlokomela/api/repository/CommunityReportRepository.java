package za.co.hlokomela.api.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hlokomela.api.domain.CommunityReport;

public interface CommunityReportRepository extends JpaRepository<CommunityReport, UUID> {
    Page<CommunityReport> findByReporterIdOrderByCreatedAtDesc(UUID reporterId, Pageable pageable);
    Page<CommunityReport> findByMunicipalityIdOrderByCreatedAtDesc(UUID municipalityId, Pageable pageable);
    Optional<CommunityReport> findByReferenceIgnoreCase(String reference);
    long countByMunicipalityIdAndCreatedAtAfter(UUID municipalityId, Instant threshold);
}
