package za.co.hlokomela.api.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import za.co.hlokomela.api.service.TelemetryService;
import za.co.hlokomela.api.web.dto.TelemetryDtos.ReadingResponse;
import za.co.hlokomela.api.web.dto.TelemetryDtos.TelemetryRequest;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {
    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) { this.telemetryService = telemetryService; }

    @PostMapping("/readings")
    @ResponseStatus(HttpStatus.CREATED)
    public ReadingResponse ingest(@RequestHeader(name = "X-Device-Key", required = false) String deviceApiKey,
                                  @Valid @RequestBody TelemetryRequest request) {
        return telemetryService.ingest(deviceApiKey, request);
    }
}
