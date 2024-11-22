package com.telran.securityservice.authorization_manager;

import com.telran.securityservice.dto.AccountDocumentDto;
import com.telran.securityservice.feign.AuthenticationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;

import java.util.UUID;
import java.util.function.Supplier;


@Slf4j
public abstract class OwnerAbstractAuthorizationManager {

    protected final AuthenticationClient client;

    public OwnerAbstractAuthorizationManager(AuthenticationClient client) {
        this.client = client;
    }

    protected AuthorizationDecision checkOwnership(Supplier<Authentication> authenticationSupplier, Object identifier) {
        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        String username = authentication.getName();
        AccountDocumentDto accountDocument = null;

        if (identifier instanceof UUID accountId) {
            accountDocument = client.findById(accountId);
        } else if (identifier instanceof String login) {
            accountDocument = client.findByLogin(login);
        }

        if (accountDocument != null) {
            boolean isOwner = accountDocument.getLogin().equals(username);
            return new AuthorizationDecision(isOwner);
        }

        return new AuthorizationDecision(false);
    }
}