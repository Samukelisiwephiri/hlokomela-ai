package za.co.hlokomela.api.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import za.co.hlokomela.api.config.SmsProperties;
import za.co.hlokomela.api.domain.UserAccount;
import za.co.hlokomela.api.repository.UserAccountRepository;

/**
 * Sends SMS-style notifications by generating a human-readable alert message
 * via the configured OpenAI-compatible endpoint, then logging it for delivery.
 *
 * In production, plug in a real SMS gateway (e.g. Africa's Talking) by replacing
 * the deliverSms() implementation below with a gateway HTTP call. The notification
 * trigger logic and recipient resolution remain unchanged.
 */
@Service
public class SmsNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    private static final String SYSTEM_PROMPT =
        "You are Hlokomela AI. Write a SHORT, clear SMS alert (max 160 chars) for a water infrastructure "
        + "notification. Be direct, professional, and include only essential facts. No greetings. No sign-off.";

    private final SmsProperties properties;
    private final RestClient restClient;
    private final UserAccountRepository userAccounts;

    public SmsNotificationService(SmsProperties properties,
                                   RestClient.Builder restClientBuilder,
                                   UserAccountRepository userAccounts) {
        this.properties = properties;
        this.restClient = restClientBuilder.baseUrl(properties.getBaseUrl()).build();
        this.userAccounts = userAccounts;
    }

    /**
     * Sends an SMS notification to all active municipal operators with a phone number.
     *
     * @param municipalityId target municipality UUID
     * @param eventType      short event label, e.g. "NEW_INCIDENT"
     * @param rawMessage     plain-language description of the event
     */
    public void notifyMunicipality(java.util.UUID municipalityId, String eventType, String rawMessage) {
        if (!properties.isEnabled()) {
            log.debug("SMS disabled — skipping notification: [{}] {}", eventType, rawMessage);
            return;
        }

        List<UserAccount> recipients = userAccounts.findByMunicipalityIdAndActiveTrue(municipalityId)
            .stream()
            .filter(u -> u.getPhone() != null && !u.getPhone().isBlank())
            .toList();

        if (recipients.isEmpty()) {
            log.info("SMS [{}] — no recipients with phone numbers in municipality {}", eventType, municipalityId);
            return;
        }

        String smsText = buildSmsText(eventType, rawMessage);
        for (UserAccount recipient : recipients) {
            deliverSms(recipient.getPhone(), smsText, eventType);
        }
    }

    private String buildSmsText(String eventType, String rawMessage) {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            return "[Hlokomela AI] " + eventType + ": " + rawMessage;
        }
        try {
            var response = restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + properties.getApiKey())
                .body(Objects.requireNonNull(Map.of(
                    "model", properties.getModel(),
                    "temperature", 0.1,
                    "max_tokens", 80,
                    "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", eventType + ": " + rawMessage)
                    )
                )))
                .retrieve()
                .body(com.fasterxml.jackson.databind.JsonNode.class);

            String text = response == null ? null : response.at("/choices/0/message/content").asText(null);
            if (text != null && !text.isBlank()) {
                return text.trim().length() > 160 ? text.trim().substring(0, 157) + "..." : text.trim();
            }
        } catch (RestClientException e) {
            log.warn("AI SMS text generation failed, using fallback: {}", e.getMessage());
        }
        return "[Hlokomela AI] " + eventType + ": " + rawMessage;
    }

    /**
     * Delivers the SMS to a recipient phone number.
     *
     * Currently logs the delivery for integration testing. Replace the body of
     * this method with an actual SMS gateway call (Africa's Talking, Twilio, etc.)
     * without changing the caller code.
     */
    private void deliverSms(String phone, String text, String eventType) {
        // TODO: Replace with actual SMS gateway HTTP call when gateway credentials are available.
        // Example for Africa's Talking:
        //   africasTalkingClient.post("/messaging").body(Map.of("to", phone, "message", text)).retrieve();
        log.info("SMS DELIVERY [{}] to={} message=\"{}\"", eventType, phone, text);
    }
}
