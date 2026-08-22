package za.co.hlokomela.api.service;

import java.time.Instant;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.hlokomela.api.domain.CommunityReport;
import za.co.hlokomela.api.domain.PipeAsset;
import za.co.hlokomela.api.domain.ReportStatus;
import za.co.hlokomela.api.domain.Role;
import za.co.hlokomela.api.domain.UserAccount;
import za.co.hlokomela.api.exception.ForbiddenOperationException;
import za.co.hlokomela.api.exception.ResourceNotFoundException;
import za.co.hlokomela.api.repository.CommunityReportRepository;
import za.co.hlokomela.api.repository.PipeAssetRepository;
import za.co.hlokomela.api.service.RiskAssessmentService.RiskAssessment;
import za.co.hlokomela.api.web.dto.ReportDtos.CreateReportRequest;

@Service
public class ReportService {
    private final CommunityReportRepository reports;
    private final PipeAssetRepository pipes;
    private final CurrentUserService currentUsers;
    private final RiskAssessmentService riskAssessmentService;
    private final IncidentService incidentService;
    private final LocalFileStorageService storage;
    private final ReferenceGenerator references;

    public ReportService(CommunityReportRepository reports, PipeAssetRepository pipes,
                         CurrentUserService currentUsers, RiskAssessmentService riskAssessmentService,
                         IncidentService incidentService, LocalFileStorageService storage,
                         ReferenceGenerator references) {
        this.reports = reports;
        this.pipes = pipes;
        this.currentUsers = currentUsers;
        this.riskAssessmentService = riskAssessmentService;
        this.incidentService = incidentService;
        this.storage = storage;
        this.references = references;
    }

    @Transactional
    public CommunityReport submit(UserAccount account, CreateReportRequest request, MultipartFile photo) {
        currentUsers.requireOneOf(account, Role.COMMUNITY_MEMBER);
        PipeAsset pipe = resolvePipe(account, request.pipeCode());
        RiskAssessment assessment = riskAssessmentService.assessReport(request.type(), request.description());
        String reference = references.next("RPT");
        String storageKey = storage.storeImage(photo);
        String photoUrl = storageKey == null ? null : "/api/v1/community-reports/" + reference + "/photo";
        CommunityReport report = new CommunityReport(reference, account, account.getMunicipality(), pipe, request.type(),
            request.location().trim(), request.latitude(), request.longitude(), request.description().trim(), photoUrl,
            storageKey, Instant.now(), assessment.riskLevel(), assessment.score());
        CommunityReport saved = reports.save(report);
        incidentService.createFromCommunityReport(saved, assessment);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<CommunityReport> mine(UserAccount account, Pageable pageable) {
        return reports.findByReporterIdOrderByCreatedAtDesc(account.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<CommunityReport> municipalQueue(UserAccount account, Pageable pageable) {
        requireStaff(account);
        return reports.findByMunicipalityIdOrderByCreatedAtDesc(account.getMunicipality().getId(), pageable);
    }

    @Transactional(readOnly = true)
    public CommunityReport getAccessible(UserAccount account, String reference) {
        CommunityReport report = reports.findByReferenceIgnoreCase(reference)
            .orElseThrow(() -> new ResourceNotFoundException("Community report was not found"));
        if (report.getReporter().getId().equals(account.getId())) return report;
        if (currentUsers.isMunicipalStaff(account)
            && report.getMunicipality().getId().equals(account.getMunicipality().getId())) return report;
        throw new ForbiddenOperationException("You cannot access this community report");
    }

    @Transactional
    public CommunityReport updateStatus(UserAccount account, String reference, ReportStatus status) {
        requireStaff(account);
        CommunityReport report = getAccessible(account, reference);
        report.setStatus(status);
        return reports.save(report);
    }

    @Transactional(readOnly = true)
    public PhotoDownload downloadPhoto(UserAccount account, String reference) {
        CommunityReport report = getAccessible(account, reference);
        LocalFileStorageService.StoredFile file = storage.load(report.getPhotoStorageKey());
        return new PhotoDownload(file.resource(), file.contentType());
    }

    private PipeAsset resolvePipe(UserAccount account, String pipeCode) {
        if (!StringUtils.hasText(pipeCode)) return null;
        return pipes.findByMunicipalityIdAndCodeIgnoreCase(account.getMunicipality().getId(), pipeCode.trim())
            .orElseThrow(() -> new ResourceNotFoundException("The selected pipe was not found in your municipality"));
    }

    private void requireStaff(UserAccount account) {
        currentUsers.requireOneOf(account, Role.MUNICIPAL_OPERATOR, Role.ADMIN);
    }

    public record PhotoDownload(Resource resource, MediaType contentType) { }
}
