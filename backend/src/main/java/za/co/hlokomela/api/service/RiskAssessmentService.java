package za.co.hlokomela.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import za.co.hlokomela.api.domain.IncidentType;
import za.co.hlokomela.api.domain.PipeAsset;
import za.co.hlokomela.api.domain.ReportType;
import za.co.hlokomela.api.domain.RiskLevel;

/**
 * Explainable bootstrap risk model. It deliberately uses deterministic, auditable signals until
 * sufficient labelled operational data is available to train and validate a municipality-specific model.
 */
@Service
public class RiskAssessmentService {
    public RiskAssessment assessTelemetry(PipeAsset pipe, double flowRate, double pressure, double vibration) {
        List<String> signals = new ArrayList<>();
        double pressureRisk = 0;
        if (pressure < pipe.getMinimumSafePressure()) {
            double deficit = (pipe.getMinimumSafePressure() - pressure) / Math.max(pipe.getMinimumSafePressure(), 0.1);
            pressureRisk = Math.min(55, deficit * 55);
            signals.add("pressure is below the safe threshold");
        }

        double flowRisk = 0;
        double flowLimit = pipe.getBaselineFlowRate() * 1.15;
        if (flowRate > flowLimit) {
            double excess = (flowRate - flowLimit) / Math.max(pipe.getBaselineFlowRate(), 0.1);
            flowRisk = Math.min(25, excess * 45);
            signals.add("flow is materially above the baseline");
        }

        double vibrationRisk = 0;
        if (vibration > pipe.getMaximumSafeVibration()) {
            double excess = (vibration - pipe.getMaximumSafeVibration()) / Math.max(pipe.getMaximumSafeVibration(), 0.1);
            vibrationRisk = Math.min(35, excess * 35);
            signals.add("vibration exceeds the normal operating range");
        }

        double score = round(Math.min(100, pressureRisk + flowRisk + vibrationRisk));
        RiskLevel level = riskLevel(score);
        boolean anomaly = level != RiskLevel.LOW;
        IncidentType type = pressureRisk > 0 && vibrationRisk > 15 ? IncidentType.BURST_RISK
            : pressureRisk > 0 ? IncidentType.PRESSURE_DROP
            : vibrationRisk > 0 ? IncidentType.HIGH_VIBRATION : IncidentType.LEAK;
        String summary = signals.isEmpty()
            ? "Reading is within the configured baseline and safety thresholds."
            : "Risk score is based on " + String.join(", ", signals) + ".";
        String action = switch (level) {
            case CRITICAL -> "Dispatch an emergency maintenance team immediately and isolate the affected section if safe.";
            case HIGH -> "Inspect the pipe within four hours and prepare a maintenance crew for dispatch.";
            case MEDIUM -> "Schedule a field inspection within 24 hours and monitor the next readings closely.";
            case LOW -> "Continue normal monitoring.";
        };
        double confidence = signals.isEmpty() ? 0.75 : Math.min(0.96, 0.78 + signals.size() * 0.06);
        return new RiskAssessment(level, score, round(confidence), anomaly, summary, type, action,
            round(score * Math.max(pipe.getBaselineFlowRate(), 1) * 12));
    }

    public RiskAssessment assessReport(ReportType type, String description) {
        String normalized = description == null ? "" : description.toLowerCase(Locale.ROOT);
        double score = switch (type) {
            case BURST_PIPE -> 88;
            case LEAK -> 66;
            case LOW_PRESSURE -> 52;
            case NO_WATER -> 58;
            case WATER_QUALITY -> 72;
            case OTHER -> 38;
        };
        if (normalized.matches(".*(flood|school|hospital|road|major|rapid|gushing|danger).*")) {
            score += 12;
        }
        score = round(Math.min(100, score));
        RiskLevel level = riskLevel(score);
        IncidentType incidentType = switch (type) {
            case BURST_PIPE -> IncidentType.BURST_RISK;
            case LEAK -> IncidentType.LEAK;
            case LOW_PRESSURE -> IncidentType.LOW_WATER_PRESSURE;
            case NO_WATER -> IncidentType.NO_WATER;
            case WATER_QUALITY -> IncidentType.WATER_QUALITY;
            case OTHER -> IncidentType.COMMUNITY_REPORT;
        };
        String action = level.ordinal() >= RiskLevel.HIGH.ordinal()
            ? "Review the report immediately and dispatch a crew if the location is confirmed."
            : "Review the report during the next operations triage and correlate it with nearby sensors.";
        return new RiskAssessment(level, score, 0.72, true,
            "Urgency was assessed from the reported issue type and safety-related description terms.",
            incidentType, action, round(score * 250));
    }

    private RiskLevel riskLevel(double score) {
        if (score >= 90) return RiskLevel.CRITICAL;
        if (score >= 70) return RiskLevel.HIGH;
        if (score >= 35) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public record RiskAssessment(RiskLevel riskLevel, double score, double confidence, boolean anomaly,
                                 String summary, IncidentType incidentType, String recommendedAction,
                                 double estimatedWaterLossLitres) { }
}
