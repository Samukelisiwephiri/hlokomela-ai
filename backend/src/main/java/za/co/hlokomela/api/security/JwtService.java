package za.co.hlokomela.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import za.co.hlokomela.api.config.AppSecurityProperties;

@Service
public class JwtService {
    private final AppSecurityProperties properties;
    private SecretKey signingKey;

    public JwtService(AppSecurityProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    @SuppressWarnings("unused")
    void initialize() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getJwtSecret());
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET must decode to at least 32 bytes");
        }
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public Token createToken(UserPrincipal principal) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(properties.getJwtExpirationMinutes(), ChronoUnit.MINUTES);
        String token = Jwts.builder()
            .subject(principal.email())
            .claim("role", principal.role())
            .claim("municipality", principal.municipalityCode())
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey)
            .compact();
        return new Token(token, expiresAt);
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserPrincipal principal) {
        Claims claims = parseClaims(token);
        return claims.getSubject().equalsIgnoreCase(principal.email())
            && claims.getExpiration().after(new Date())
            && principal.isEnabled();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(signingKey).build()
            .parseSignedClaims(token).getPayload();
    }

    public record Token(String value, Instant expiresAt) { }
}
