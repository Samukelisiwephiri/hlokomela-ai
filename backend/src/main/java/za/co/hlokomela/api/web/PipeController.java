package za.co.hlokomela.api.web;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import za.co.hlokomela.api.security.UserPrincipal;
import za.co.hlokomela.api.service.CurrentUserService;
import za.co.hlokomela.api.service.PipeService;
import za.co.hlokomela.api.service.ResponseMapper;
import za.co.hlokomela.api.web.dto.OperationsDtos.CreatePipeRequest;
import za.co.hlokomela.api.web.dto.OperationsDtos.PipeResponse;
import za.co.hlokomela.api.web.dto.TelemetryDtos.ReadingResponse;

@RestController
@RequestMapping("/api/v1/pipes")
public class PipeController {
    private final PipeService pipeService;
    private final CurrentUserService currentUsers;

    public PipeController(PipeService pipeService, CurrentUserService currentUsers) {
        this.pipeService = pipeService;
        this.currentUsers = currentUsers;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<PipeResponse> list(@AuthenticationPrincipal UserPrincipal principal,
                                   @RequestParam(required = false) String query) {
        return pipeService.list(currentUsers.require(principal), query).stream().map(ResponseMapper::pipe).toList();
    }

    @GetMapping("/{code}")
    @Transactional(readOnly = true)
    public PipeResponse get(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String code) {
        return ResponseMapper.pipe(pipeService.get(currentUsers.require(principal), code));
    }

    @GetMapping("/{code}/readings")
    @Transactional(readOnly = true)
    public List<ReadingResponse> readings(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String code) {
        return pipeService.latestReadings(currentUsers.require(principal), code).stream()
            .map(reading -> ResponseMapper.reading(reading, null)).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MUNICIPAL_OPERATOR','ADMIN')")
    @Transactional
    public PipeResponse create(@AuthenticationPrincipal UserPrincipal principal,
                               @Valid @RequestBody CreatePipeRequest request) {
        return ResponseMapper.pipe(pipeService.create(currentUsers.require(principal), request));
    }
}
