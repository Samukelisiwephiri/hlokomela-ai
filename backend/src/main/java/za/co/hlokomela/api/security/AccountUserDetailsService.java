package za.co.hlokomela.api.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.hlokomela.api.repository.UserAccountRepository;

@Service
public class AccountUserDetailsService implements UserDetailsService {
    private final UserAccountRepository users;

    public AccountUserDetailsService(UserAccountRepository users) {
        this.users = users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return users.findByEmailIgnoreCase(email)
            .map(UserPrincipal::from)
            .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }
}
