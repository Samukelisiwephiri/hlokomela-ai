package za.co.hlokomela.api.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.hlokomela.api.domain.UserAccount;

public record UserPrincipal(UUID id, String email, String passwordHash, UUID municipalityId,
                            String municipalityCode, String role, boolean active) implements UserDetails {
    public static UserPrincipal from(UserAccount account) {
        return new UserPrincipal(account.getId(), account.getEmail(), account.getPasswordHash(),
            account.getMunicipality().getId(), account.getMunicipality().getCode(),
            account.getRole().name(), account.isActive());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
