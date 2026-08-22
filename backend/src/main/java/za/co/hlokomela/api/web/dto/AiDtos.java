package za.co.hlokomela.api.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AiDtos {
    private AiDtos() { }

    public record AssistantRequest(@NotBlank @Size(max = 1000) String question,
                                   @Size(max = 8) String language) { }

    public record AssistantResponse(String answer, String model, boolean live) { }
}
