package za.co.hlokomela.api.service;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.co.hlokomela.api.domain.Role;
import za.co.hlokomela.api.domain.UserAccount;
import za.co.hlokomela.api.exception.ForbiddenOperationException;
import za.co.hlokomela.api.exception.ResourceNotFoundException;
import za.co.hlokomela.api.repository.UserAccountRepository;
import za.co.hlokomela.api.security.UserPrincipal;

@Service
public class CurrentUserService {
    private final UserAccountRepository users;

    public CurrentUserService(UserAccountRepository users) {
        this.users = users;
    }

    @Transactional(readOnly = true)
    public UserAccount require(UserPrincipal principal) {
        UserAccount account = users.findById(Objects.requireNonNull(principal.id()))
            .orElseThrow(() -> new ResourceNotFoundException("Authenticated account no longer exists"));
        if (!account.isActive()) {
            throw new ForbiddenOperationException("This account has been disabled");
        }
        return account;
    }

    public void requireOneOf(UserAccount account, Role... allowedRoles) {
        boolean allowed = Arrays.stream(allowedRoles).anyMatch(role -> role == account.getRole());
        if (!allowed) {
            throw new ForbiddenOperationException("Your account role cannot perform this action");
        }
    }

    public boolean isMunicipalStaff(UserAccount account) {
        return account.getRole() == Role.MUNICIPAL_OPERATOR || account.getRole() == Role.ADMIN;
    }
}
