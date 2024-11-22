package com.telran.authenticationservice.controller;

import com.telran.authenticationservice.persistence.AccountDocument;
import com.telran.authenticationservice.service.AuthenticationJWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/secret")
@RequiredArgsConstructor
public class SecretAuthenticationController {

    private final AuthenticationJWTService authenticationService;

    @GetMapping("/id/{accountId}")
    public AccountDocument getAccountById(@PathVariable UUID accountId) {
        return authenticationService.findAccountDocumentByAccountId(accountId);
    }

    @GetMapping("/login/{login}")
    public AccountDocument getAccountByLogin(@PathVariable String login) {
        return authenticationService.findAccountDocumentByLogin(login);
    }
}
