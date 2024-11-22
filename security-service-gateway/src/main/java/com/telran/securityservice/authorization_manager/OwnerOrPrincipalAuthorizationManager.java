package com.telran.securityservice.authorization_manager;

import com.telran.securityservice.dto.Roles;
import com.telran.securityservice.feign.AuthenticationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Component
public class OwnerOrPrincipalAuthorizationManager extends OwnerAbstractAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    public OwnerOrPrincipalAuthorizationManager(AuthenticationClient client) {
        super(client);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        String pathVariable = context.getVariables().get("id");
        Object accountIdentity;
        if (pathVariable == null) {
            accountIdentity = context.getVariables().get("login");
        } else
            accountIdentity = UUID.fromString(pathVariable);

        AuthorizationDecision ownershipDecision = checkOwnership(authenticationSupplier, accountIdentity);
        if (ownershipDecision.isGranted()) {
            return ownershipDecision;
        }

        Authentication authentication = authenticationSupplier.get();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + Roles.PRINCIPAL));

        return new AuthorizationDecision(isAdmin);
    }
}