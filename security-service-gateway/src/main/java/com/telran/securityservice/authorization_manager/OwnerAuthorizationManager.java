package com.telran.securityservice.authorization_manager;

import com.telran.securityservice.feign.AuthenticationClient;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@Component
public class OwnerAuthorizationManager extends OwnerAbstractAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    public OwnerAuthorizationManager(AuthenticationClient client) {
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

        return checkOwnership(authenticationSupplier, accountIdentity);
    }

}