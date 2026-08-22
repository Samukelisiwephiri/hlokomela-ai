package za.co.hlokomela.api.web;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import za.co.hlokomela.api.security.UserPrincipal;
import za.co.hlokomela.api.service.CurrentUserService;
import za.co.hlokomela.api.service.ReportService;
import za.co.hlokomela.api.service.ResponseMapper;
import za.co.hlokomela.api.web.dto.ApiDtos.PageResponse;
import za.co.hlokomela.api.web.dto.ReportDtos.CreateReportRequest;
import za.co.hlokomela.api.web.dto.ReportDtos.ReportResponse;
import za.co.hlokomela.api.web.dto.ReportDtos.ReportStatusUpdateRequest;

@RestController
@RequestMapping("/api/v1/community-reports")
public class CommunityReportController {
    private final ReportService reportService;
    private final CurrentUserService currentUsers;

    public CommunityReportController(ReportService reportService, CurrentUserService currentUsers) {
        this.reportService = reportService;
        this.currentUsers = currentUsers;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COMMUNITY_MEMBER')")
    @Transactional
    public ResponseEntity<ReportResponse> submit(@AuthenticationPrincipal UserPrincipal principal,
                                                 @Valid @RequestBody CreateReportRequest request) {
        ReportResponse response = ResponseMapper.report(reportService.submit(currentUsers.require(principal), request, null));
        return ResponseEntity.created(URI.create("/api/v1/community-reports/" + response.reference())).body(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('COMMUNITY_MEMBER')")
    @Transactional
    public ResponseEntity<ReportResponse> submitWithPhoto(@AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestPart("report") CreateReportRequest request,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        ReportResponse response = ResponseMapper.report(reportService.submit(currentUsers.require(principal), request, photo));
        return ResponseEntity.created(URI.create("/api/v1/community-reports/" + response.reference())).body(response);
    }

    @GetMapping("/mine")
    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> mine(@AuthenticationPrincipal UserPrincipal principal,
                                             @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(reportService.mine(currentUsers.require(principal), pageable).map(ResponseMapper::report));
    }

    @GetMapping("/municipal")
    @PreAuthorize("hasAnyRole('MUNICIPAL_OPERATOR','ADMIN')")
    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> municipalQueue(@AuthenticationPrincipal UserPrincipal principal,
                                                        @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(reportService.municipalQueue(currentUsers.require(principal), pageable)
            .map(ResponseMapper::report));
    }

    @GetMapping("/{reference}")
    @Transactional(readOnly = true)
    public ReportResponse get(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String reference) {
        return ResponseMapper.report(reportService.getAccessible(currentUsers.require(principal), reference));
    }

    @PatchMapping("/{reference}/status")
    @PreAuthorize("hasAnyRole('MUNICIPAL_OPERATOR','ADMIN')")
    @Transactional
    public ReportResponse updateStatus(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String reference,
                                       @Valid @RequestBody ReportStatusUpdateRequest request) {
        return ResponseMapper.report(reportService.updateStatus(currentUsers.require(principal), reference, request.status()));
    }

    @GetMapping("/{reference}/photo")
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> photo(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String reference) {
        ReportService.PhotoDownload photo = reportService.downloadPhoto(currentUsers.require(principal), reference);
        return ResponseEntity.ok().contentType(photo.contentType())
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=report-photo")
            .body(photo.resource());
    }
}
