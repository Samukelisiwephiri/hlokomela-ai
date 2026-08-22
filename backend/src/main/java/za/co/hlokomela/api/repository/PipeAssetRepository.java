package za.co.hlokomela.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hlokomela.api.domain.PipeAsset;

public interface PipeAssetRepository extends JpaRepository<PipeAsset, UUID> {
    List<PipeAsset> findByMunicipalityIdOrderByCodeAsc(UUID municipalityId);
    Optional<PipeAsset> findByMunicipalityIdAndCodeIgnoreCase(UUID municipalityId, String code);
    Optional<PipeAsset> findByDeviceIdIgnoreCase(String deviceId);
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByDeviceIdIgnoreCase(String deviceId);
}
