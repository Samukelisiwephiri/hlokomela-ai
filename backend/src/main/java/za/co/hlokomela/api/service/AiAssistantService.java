package za.co.hlokomela.api.service;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;

import za.co.hlokomela.api.config.AiProviderProperties;
import za.co.hlokomela.api.web.dto.AiDtos.AssistantRequest;
import za.co.hlokomela.api.web.dto.AiDtos.AssistantResponse;

/**
 * Calls the configured AI provider without exposing credentials to clients.
 */
@Service
public class AiAssistantService {
    private static final String SYSTEM_PROMPT = "You are Hlokomela AI, a concise and careful water-service assistant "
        + "for South African communities and municipal operators. Give practical advice about leaks, pressure, "
        + "water quality, conservation, and reporting. Never claim to have dispatched a team or confirmed a burst "
        + "unless the application data says so. For emergencies, advise the user to stay clear and contact their "
        + "municipality. Answer in the requested language when possible.";

    private final RestClient restClient;
    private final AiProviderProperties properties;

    public AiAssistantService(RestClient.Builder restClientBuilder, AiProviderProperties properties) {
        this.restClient = restClientBuilder.baseUrl(Objects.requireNonNull(properties.getBaseUrl())).build();
        this.properties = properties;
    }

    /**
     * Returns a provider answer or a safe fallback when live AI is unavailable.
     */
    public AssistantResponse answer(AssistantRequest request) {
        if (!properties.isEnabled()
            || properties.getApiKey() == null
            || properties.getApiKey().isBlank()) {
            return new AssistantResponse(
                "The live AI assistant is not configured yet. Please use the report form for urgent water problems.",
                properties.getModel(), false);
        }

        String language = request.language() == null || request.language().isBlank() ? "English" : request.language();
        try {
            JsonNode response = restClient.post()
                .uri("/chat/completions")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .header("Authorization", "Bearer " + properties.getApiKey())
                .body(Objects.requireNonNull(Map.of(
                    "model", properties.getModel(),
                    "temperature", 0.2,
                    "max_tokens", 300,
                    "messages", java.util.List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content",
                            "Language: " + language + "\\nQuestion: " + request.question())
                    ))))
                .retrieve()
                .body(JsonNode.class);
            String answer = response == null ? null : response.at("/choices/0/message/content").asText(null);
            if (answer == null || answer.isBlank()) {
                throw new IllegalStateException("Provider returned no answer");
            }
            return new AssistantResponse(answer.trim(), properties.getModel(), true);
        } catch (RestClientException | IllegalStateException exception) {
            return new AssistantResponse(
                "The live assistant is temporarily unavailable. Please describe the issue in a report so the municipality can review it.",
                properties.getModel(), false);
        }
    }
}
