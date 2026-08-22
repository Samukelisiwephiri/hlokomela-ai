package za.co.hlokomela.api.web;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import za.co.hlokomela.api.security.UserPrincipal;
import za.co.hlokomela.api.service.AlertService;
import za.co.hlokomela.api.service.CurrentUserService;
import za.co.hlokomela.api.service.ResponseMapper;
import za.co.hlokomela.api.web.dto.OperationsDtos.AlertResponse;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {
    private final AlertService alertService;
    private final CurrentUserService currentUsers;

    public AlertController(AlertService alertService, CurrentUserService currentUsers) {
        this.alertService = alertService;
        this.currentUsers = currentUsers;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<AlertResponse> list(@AuthenticationPrincipal UserPrincipal principal,
                                    @RequestParam(defaultValue = "20") int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return alertService.list(currentUsers.require(principal), PageRequest.of(0, safeLimit)).stream()
            .map(ResponseMapper::alert).toList();
    }

    @PatchMapping("/{id}/read")
    @Transactional
    public AlertResponse markRead(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID id) {
        return ResponseMapper.alert(alertService.markRead(currentUsers.require(principal), id));
    }
}
