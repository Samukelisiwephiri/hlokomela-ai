package za.co.hlokomela.api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import za.co.hlokomela.api.domain.Municipality;
import za.co.hlokomela.api.domain.Role;
import za.co.hlokomela.api.domain.UserAccount;
import za.co.hlokomela.api.exception.ConflictException;
import za.co.hlokomela.api.exception.ResourceNotFoundException;
import za.co.hlokomela.api.exception.UnauthorizedException;
import za.co.hlokomela.api.repository.MunicipalityRepository;
import za.co.hlokomela.api.repository.UserAccountRepository;
import za.co.hlokomela.api.security.JwtService;
import za.co.hlokomela.api.security.UserPrincipal;
import za.co.hlokomela.api.web.dto.AuthDtos.AuthResponse;
import za.co.hlokomela.api.web.dto.AuthDtos.LoginRequest;
import za.co.hlokomela.api.web.dto.AuthDtos.RegisterRequest;
import za.co.hlokomela.api.web.dto.AuthDtos.UserResponse;

@Service
public class AuthService {
    private final UserAccountRepository users;
    private final MunicipalityRepository municipalities;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserAccountRepository users, MunicipalityRepository municipalities,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.municipalities = municipalities;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (users.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("An account already exists for this email address");
        }
        validatePassword(request.password());
        Municipality municipality = resolveMunicipality(request.municipalityCode());
        UserAccount account = users.save(new UserAccount(request.email(), passwordEncoder.encode(request.password()),
            request.firstName().trim(), request.lastName().trim(), normalize(request.phone()),
            Role.COMMUNITY_MEMBER, municipality));
        return tokenResponse(account);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        UserAccount account = users.findByEmailIgnoreCase(request.email())
            .orElseThrow(() -> new UnauthorizedException("Invalid email address or password"));
        if (!account.isActive() || !passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email address or password");
        }
        return tokenResponse(account);
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(UserPrincipal principal) {
        UserAccount account = users.findById(principal.id())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return ResponseMapper.user(account);
    }

    private AuthResponse tokenResponse(UserAccount account) {
        JwtService.Token token = jwtService.createToken(UserPrincipal.from(account));
        return new AuthResponse(token.value(), "Bearer", token.expiresAt(), ResponseMapper.user(account));
    }

    private Municipality resolveMunicipality(String municipalityCode) {
        if (StringUtils.hasText(municipalityCode)) {
            return municipalities.findByCodeIgnoreCase(municipalityCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Municipality code was not found"));
        }
        return municipalities.findByCodeIgnoreCase("UMK")
            .orElseGet(() -> municipalities.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No municipality is configured")));
    }

    private void validatePassword(String password) {
        boolean upper = password.chars().anyMatch(Character::isUpperCase);
        boolean lower = password.chars().anyMatch(Character::isLowerCase);
        boolean digit = password.chars().anyMatch(Character::isDigit);
        boolean special = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));
        if (!(upper && lower && digit && special)) {
            throw new IllegalArgumentException("Password must include upper-case, lower-case, numeric, and special characters");
        }
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
