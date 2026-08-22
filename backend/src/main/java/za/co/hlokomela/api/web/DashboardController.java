package za.co.hlokomela.api.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.hlokomela.api.security.UserPrincipal;
import za.co.hlokomela.api.service.CurrentUserService;
import za.co.hlokomela.api.service.DashboardService;
import za.co.hlokomela.api.web.dto.OperationsDtos.DashboardSummaryResponse;

@RestController
@RequestMapping("/api/v1/dashboard")
@PreAuthorize("hasAnyRole('MUNICIPAL_OPERATOR','ADMIN')")
public class DashboardController {
    private final DashboardService dashboardService;
    private final CurrentUserService currentUsers;

    public DashboardController(DashboardService dashboardService, CurrentUserService currentUsers) {
        this.dashboardService = dashboardService;
        this.currentUsers = currentUsers;
    }

    @GetMapping("/summary")
    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary(@AuthenticationPrincipal UserPrincipal principal) {
        return dashboardService.summary(currentUsers.require(principal));
    }
}
