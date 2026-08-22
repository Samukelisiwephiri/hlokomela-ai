package za.co.hlokomela.api.web;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.hlokomela.api.security.UserPrincipal;
import za.co.hlokomela.api.service.CurrentUserService;
import za.co.hlokomela.api.service.IncidentService;
import za.co.hlokomela.api.service.ResponseMapper;
import za.co.hlokomela.api.web.dto.ApiDtos.PageResponse;
import za.co.hlokomela.api.web.dto.OperationsDtos.DispatchRequest;
import za.co.hlokomela.api.web.dto.OperationsDtos.IncidentResponse;
import za.co.hlokomela.api.web.dto.OperationsDtos.UpdateIncidentStatusRequest;
import za.co.hlokomela.api.web.dto.OperationsDtos.WorkOrderResponse;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyRole('MUNICIPAL_OPERATOR','ADMIN')")
public class OperationsController {
    private final IncidentService incidentService;
    private final CurrentUserService currentUsers;

    public OperationsController(IncidentService incidentService, CurrentUserService currentUsers) {
        this.incidentService = incidentService;
        this.currentUsers = currentUsers;
    }

    @GetMapping("/incidents")
    @Transactional(readOnly = true)
    public PageResponse<IncidentResponse> incidents(@AuthenticationPrincipal UserPrincipal principal,
                                                    @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(incidentService.list(currentUsers.require(principal), pageable).map(ResponseMapper::incident));
    }

    @GetMapping("/incidents/{reference}")
    @Transactional(readOnly = true)
    public IncidentResponse incident(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String reference) {
        return ResponseMapper.incident(incidentService.getForMunicipality(currentUsers.require(principal), reference));
    }

    @PatchMapping("/incidents/{reference}/status")
    @Transactional
    public IncidentResponse updateIncidentStatus(@AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String reference, @Valid @RequestBody UpdateIncidentStatusRequest request) {
        return ResponseMapper.incident(incidentService.updateStatus(currentUsers.require(principal), reference, request.status()));
    }

    @PostMapping("/incidents/{reference}/dispatch")
    @Transactional
    public WorkOrderResponse dispatch(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String reference,
                                      @Valid @RequestBody DispatchRequest request) {
        return ResponseMapper.workOrder(incidentService.dispatch(currentUsers.require(principal), reference, request));
    }

    @GetMapping("/maintenance/work-orders")
    @Transactional(readOnly = true)
    public List<WorkOrderResponse> workOrders(@AuthenticationPrincipal UserPrincipal principal) {
        return incidentService.listWorkOrders(currentUsers.require(principal)).stream().map(ResponseMapper::workOrder).toList();
    }
}
