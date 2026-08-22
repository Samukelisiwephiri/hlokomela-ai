package za.co.hlokomela.api.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.hlokomela.api.domain.Incident;
import za.co.hlokomela.api.domain.PipeAsset;
import za.co.hlokomela.api.domain.SensorReading;
import za.co.hlokomela.api.exception.ResourceNotFoundException;
import za.co.hlokomela.api.repository.PipeAssetRepository;
import za.co.hlokomela.api.repository.SensorReadingRepository;
import za.co.hlokomela.api.service.RiskAssessmentService.RiskAssessment;
import za.co.hlokomela.api.web.dto.TelemetryDtos.ReadingResponse;
import za.co.hlokomela.api.web.dto.TelemetryDtos.TelemetryRequest;

@Service
public class TelemetryService {
    private final DeviceKeyValidator deviceKeyValidator;
    private final PipeAssetRepository pipes;
    private final SensorReadingRepository readings;
    private final RiskAssessmentService riskAssessmentService;
    private final IncidentService incidentService;

    public TelemetryService(DeviceKeyValidator deviceKeyValidator, PipeAssetRepository pipes,
                            SensorReadingRepository readings, RiskAssessmentService riskAssessmentService,
                            IncidentService incidentService) {
        this.deviceKeyValidator = deviceKeyValidator;
        this.pipes = pipes;
        this.readings = readings;
        this.riskAssessmentService = riskAssessmentService;
        this.incidentService = incidentService;
    }

    @Transactional
    public ReadingResponse ingest(String deviceApiKey, TelemetryRequest request) {
        deviceKeyValidator.requireValid(deviceApiKey);
        PipeAsset pipe = pipes.findByDeviceIdIgnoreCase(request.deviceId())
            .orElseThrow(() -> new ResourceNotFoundException("No registered pipe matches this device ID"));
        Instant now = Instant.now();
        Instant recordedAt = request.timestamp() == null ? now : request.timestamp();
        if (recordedAt.isAfter(now.plus(5, ChronoUnit.MINUTES))
            || recordedAt.isBefore(now.minus(30, ChronoUnit.DAYS))) {
            throw new IllegalArgumentException("Telemetry timestamp is outside the accepted ingestion window");
        }
        RiskAssessment assessment = riskAssessmentService.assessTelemetry(pipe, request.flow(), request.pressure(), request.vibration());
        SensorReading reading = readings.save(new SensorReading(pipe, request.deviceId(), request.flow(), request.pressure(),
            request.vibration(), recordedAt, assessment.riskLevel(), assessment.score(), assessment.anomaly(), assessment.summary()));
        pipe.setCurrentRiskLevel(assessment.riskLevel());
        pipe.setCurrentRiskScore(assessment.score());
        pipe.setLatestRecommendation(assessment.recommendedAction());
        pipe.setLastReadingAt(recordedAt);
        pipes.save(pipe);
        Incident incident = incidentService.createOrUpdateFromTelemetry(pipe, assessment);
        return ResponseMapper.reading(reading, incident == null ? null : incident.getReference());
    }
}
