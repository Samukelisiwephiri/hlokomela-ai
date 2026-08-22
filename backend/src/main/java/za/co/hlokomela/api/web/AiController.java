package za.co.hlokomela.api.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import za.co.hlokomela.api.service.AiAssistantService;
import za.co.hlokomela.api.web.dto.AiDtos.AssistantRequest;
import za.co.hlokomela.api.web.dto.AiDtos.AssistantResponse;

/**
 * Exposes the authenticated Hlokomela AI assistant endpoint.
 */
@RestController
@RequestMapping("/api/v1/ai")
@PreAuthorize("hasAnyRole('COMMUNITY_MEMBER','MUNICIPAL_OPERATOR','ADMIN')")
public class AiController {
    private final AiAssistantService assistant;

    public AiController(AiAssistantService assistant) {
        this.assistant = assistant;
    }

    /**
     * Answers a user question through the configured AI provider.
     */
    @PostMapping("/assistant")
    public AssistantResponse answer(@Valid @RequestBody AssistantRequest request) {
        return assistant.answer(request);
    }
}
