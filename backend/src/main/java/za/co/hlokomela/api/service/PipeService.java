package za.co.hlokomela.api.service;

import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import za.co.hlokomela.api.domain.PipeAsset;
import za.co.hlokomela.api.domain.Role;
import za.co.hlokomela.api.domain.SensorReading;
import za.co.hlokomela.api.domain.UserAccount;
import za.co.hlokomela.api.exception.ConflictException;
import za.co.hlokomela.api.exception.ResourceNotFoundException;
import za.co.hlokomela.api.repository.PipeAssetRepository;
import za.co.hlokomela.api.repository.SensorReadingRepository;
import za.co.hlokomela.api.web.dto.OperationsDtos.CreatePipeRequest;

@Service
public class PipeService {
    private final PipeAssetRepository pipes;
    private final SensorReadingRepository readings;
    private final CurrentUserService currentUsers;

    public PipeService(PipeAssetRepository pipes, SensorReadingRepository readings, CurrentUserService currentUsers) {
        this.pipes = pipes;
        this.readings = readings;
        this.currentUsers = currentUsers;
    }

    @Transactional(readOnly = true)
    public List<PipeAsset> list(UserAccount account, String query) {
        List<PipeAsset> assets = pipes.findByMunicipalityIdOrderByCodeAsc(account.getMunicipality().getId());
        if (!StringUtils.hasText(query)) return assets;
        String normalized = query.trim().toLowerCase(Locale.ROOT);
        return assets.stream().filter(pipe -> contains(pipe.getCode(), normalized)
            || contains(pipe.getLocationName(), normalized) || contains(pipe.getWard(), normalized)).toList();
    }

    @Transactional(readOnly = true)
    public PipeAsset get(UserAccount account, String code) {
        return pipes.findByMunicipalityIdAndCodeIgnoreCase(account.getMunicipality().getId(), code)
            .orElseThrow(() -> new ResourceNotFoundException("Pipe was not found"));
    }

    @Transactional
    public PipeAsset create(UserAccount account, CreatePipeRequest request) {
        currentUsers.requireOneOf(account, Role.MUNICIPAL_OPERATOR, Role.ADMIN);
        if (pipes.existsByCodeIgnoreCase(request.code())) throw new ConflictException("A pipe already uses this code");
        if (StringUtils.hasText(request.deviceId()) && pipes.existsByDeviceIdIgnoreCase(request.deviceId().trim())) {
            throw new ConflictException("A sensor device is already linked to another pipe");
        }
        PipeAsset pipe = new PipeAsset(request.code().trim().toUpperCase(Locale.ROOT), normalize(request.deviceId()),
            account.getMunicipality(), request.locationName().trim(), normalize(request.ward()), request.latitude(),
            request.longitude(), request.baselineFlowRate(), request.baselinePressure(), request.minimumSafePressure(),
            request.maximumSafeVibration());
        return pipes.save(pipe);
    }

    @Transactional(readOnly = true)
    public List<SensorReading> latestReadings(UserAccount account, String code) {
        return readings.findTop50ByPipeIdOrderByRecordedAtDesc(get(account, code).getId());
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
