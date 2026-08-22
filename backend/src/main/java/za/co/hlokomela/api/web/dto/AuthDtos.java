package za.co.hlokomela.api.web.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import za.co.hlokomela.api.domain.Role;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterRequest(
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 10, max = 72) String password,
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @Size(max = 40) String phone,
        @Size(max = 24) @Pattern(regexp = "^[A-Za-z0-9-]*$", message = "must contain only letters, numbers, and hyphens") String municipalityCode
    ) { }

    public record LoginRequest(
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 72) String password
    ) { }

    public record UserResponse(UUID id, String email, String firstName, String lastName,
                               String phone, Role role, String municipalityCode, String municipalityName) { }

    public record AuthResponse(String accessToken, String tokenType, Instant expiresAt, UserResponse user) { }
}
