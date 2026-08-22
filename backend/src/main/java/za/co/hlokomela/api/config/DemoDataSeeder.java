package za.co.hlokomela.api.config;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import za.co.hlokomela.api.domain.Alert;
import za.co.hlokomela.api.domain.CommunityReport;
import za.co.hlokomela.api.domain.Incident;
import za.co.hlokomela.api.domain.IncidentSource;
import za.co.hlokomela.api.domain.IncidentStatus;
import za.co.hlokomela.api.domain.IncidentType;
import za.co.hlokomela.api.domain.MaintenanceWorkOrder;
import za.co.hlokomela.api.domain.Municipality;
import za.co.hlokomela.api.domain.PipeAsset;
import za.co.hlokomela.api.domain.ReportStatus;
import za.co.hlokomela.api.domain.ReportType;
import za.co.hlokomela.api.domain.RiskLevel;
import za.co.hlokomela.api.domain.Role;
import za.co.hlokomela.api.domain.SensorReading;
import za.co.hlokomela.api.domain.UserAccount;
import za.co.hlokomela.api.domain.WorkOrderStatus;
import za.co.hlokomela.api.repository.AlertRepository;
import za.co.hlokomela.api.repository.CommunityReportRepository;
import za.co.hlokomela.api.repository.IncidentRepository;
import za.co.hlokomela.api.repository.MaintenanceWorkOrderRepository;
import za.co.hlokomela.api.repository.MunicipalityRepository;
import za.co.hlokomela.api.repository.PipeAssetRepository;
import za.co.hlokomela.api.repository.SensorReadingRepository;
import za.co.hlokomela.api.repository.UserAccountRepository;

/** Local pilot data to make the dashboard useful on a clean development database. */
@Component
public class DemoDataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);
    private final DemoDataProperties properties;
    private final MunicipalityRepository municipalities;
    private final PipeAssetRepository pipes;
    private final UserAccountRepository users;
    private final SensorReadingRepository readings;
    private final CommunityReportRepository reports;
    private final IncidentRepository incidents;
    private final AlertRepository alerts;
    private final MaintenanceWorkOrderRepository workOrders;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(DemoDataProperties properties, MunicipalityRepository municipalities,
                          PipeAssetRepository pipes, UserAccountRepository users, SensorReadingRepository readings,
                          CommunityReportRepository reports, IncidentRepository incidents, AlertRepository alerts,
                          MaintenanceWorkOrderRepository workOrders, PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.municipalities = municipalities;
        this.pipes = pipes;
        this.users = users;
        this.readings = readings;
        this.reports = reports;
        this.incidents = incidents;
        this.alerts = alerts;
        this.workOrders = workOrders;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!properties.isEnabled() || municipalities.findByCodeIgnoreCase("UMK").isPresent()) {
            return;
        }
        Instant now = Instant.now();
        Municipality municipality = municipalities.save(new Municipality("UMK", "uMkhanyakude District Municipality",
            "KwaZulu-Natal", "ops@umkhanyakude.gov.za"));

        PipeAsset p101 = pipe("P-101", "PIPE-01", municipality, "Manguzi", "Ward 2", -26.996, 32.752, 35, 2.8, 1.4, 3.0);
        p101.setCurrentRiskLevel(RiskLevel.HIGH);
        p101.setCurrentRiskScore(88.0);
        p101.setLatestRecommendation("Dispatch a maintenance team within four hours and inspect the pressure anomaly.");
        p101.setLastReadingAt(now.minusSeconds(90));
        PipeAsset p102 = pipe("P-102", "PIPE-02", municipality, "Mbazwana", "Ward 5", -27.108, 32.691, 22, 3.6, 1.6, 2.8);
        p102.setLastReadingAt(now.minusSeconds(120));
        PipeAsset p103 = pipe("P-103", "PIPE-03", municipality, "Jozini", "Ward 8", -27.431, 32.066, 30, 2.5, 1.3, 3.1);
        p103.setCurrentRiskLevel(RiskLevel.MEDIUM);
        p103.setCurrentRiskScore(61.0);
        p103.setLatestRecommendation("Schedule a field inspection within 24 hours.");
        p103.setLastReadingAt(now.minusSeconds(180));
        PipeAsset p104 = pipe("P-104", "PIPE-04", municipality, "Hluhluwe", "Ward 11", -28.020, 32.269, 31, 2.7, 1.4, 3.0);
        p104.setCurrentRiskLevel(RiskLevel.HIGH);
        p104.setCurrentRiskScore(82.0);
        p104.setLatestRecommendation("Inspect vibration levels and prepare a standby repair crew.");
        p104.setLastReadingAt(now.minusSeconds(110));
        pipes.save(p101);
        pipes.save(p102);
        pipes.save(p103);
        pipes.save(p104);

        UserAccount administrator = users.save(new UserAccount("admin@hlokomela.local", passwordEncoder.encode("AdminDemo123!"),
            "Platform", "Administrator", null, Role.ADMIN, municipality));
        UserAccount operator = users.save(new UserAccount("ops@umkhanyakude.gov.za", passwordEncoder.encode("DemoPass123!"),
            "Nandi", "Mkhize", "+27 35 572 0000", Role.MUNICIPAL_OPERATOR, municipality));
        UserAccount resident = users.save(new UserAccount("community@hlokomela.local", passwordEncoder.encode("DemoPass123!"),
            "Samukelisiwe", "Dlamini", "+27 82 000 0000", Role.COMMUNITY_MEMBER, municipality));

        readings.save(new SensorReading(p101, "PIPE-01", 48.0, 0.8, 7.5, now.minusSeconds(90), RiskLevel.HIGH, 88.0,
            true, "Pressure is below the safe threshold, flow is materially above the baseline, and vibration exceeds the normal operating range."));
        readings.save(new SensorReading(p102, "PIPE-02", 22.0, 3.9, 1.2, now.minusSeconds(120), RiskLevel.LOW, 4.0,
            false, "Reading is within the configured baseline and safety thresholds."));
        readings.save(new SensorReading(p103, "PIPE-03", 36.0, 1.5, 3.8, now.minusSeconds(180), RiskLevel.MEDIUM, 61.0,
            true, "Pressure is below the safe threshold and vibration exceeds the normal operating range."));

        CommunityReport report = reports.save(new CommunityReport("RPT-DEMO001", resident, municipality, p101,
            ReportType.LEAK, "Manguzi school zone", -26.996, 32.752,
            "Water is leaking near the school and the flow is increasing rapidly.", null, null, now.minusSeconds(600),
            RiskLevel.HIGH, 78.0));
        report.setStatus(ReportStatus.IN_PROGRESS);
        reports.save(report);

        Incident incident = incidents.save(new Incident("INC-DEMO101", municipality, p101, report, IncidentSource.SENSOR,
            IncidentType.BURST_RISK, "Sensor anomaly at P-101",
            "Sustained pressure drop, elevated flow, strong vibration, and a corroborating community report indicate a likely burst risk.",
            RiskLevel.HIGH, 88.0, 0.96,
            "Dispatch a maintenance team within four hours and isolate the affected section if safe.", 18_000));
        incident.setStatus(IncidentStatus.IN_PROGRESS);
        incident.setAssignedTeam("Team 3");
        incident.setDispatchedAt(now.minusSeconds(300));
        incidents.save(incident);

        alerts.save(new Alert(municipality, incident, null, "High risk at P-101",
            "Possible burst risk near Manguzi. Team 3 has been dispatched and residents should avoid the immediate area.", RiskLevel.HIGH));
        MaintenanceWorkOrder workOrder = new MaintenanceWorkOrder("WO-DEMO101", municipality, incident, "Team 3",
            now.plusSeconds(3_600), "Inspect P-101, verify isolation options, and update the community report.");
        workOrder.setStatus(WorkOrderStatus.IN_PROGRESS);
        workOrders.save(workOrder);

        log.warn("Seeded local demo accounts. Use only for development: ops@umkhanyakude.gov.za / DemoPass123! and community@hlokomela.local / DemoPass123!");
        log.debug("Demo administrator created: {}", administrator.getEmail());
        log.debug("Demo operator created: {}", operator.getEmail());
    }

    private PipeAsset pipe(String code, String deviceId, Municipality municipality, String location, String ward,
                           double latitude, double longitude, double baselineFlow, double baselinePressure,
                           double minimumPressure, double maximumVibration) {
        return new PipeAsset(code, deviceId, municipality, location, ward, latitude, longitude, baselineFlow,
            baselinePressure, minimumPressure, maximumVibration);
    }
}
