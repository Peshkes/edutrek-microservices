package com.telran.authenticationservice.service;

import com.telran.authenticationservice.dto.AuthenticationDataDto;
import com.telran.authenticationservice.dto.JWTBodyReturnDto;
import com.telran.authenticationservice.feign.MailingClient;
import com.telran.authenticationservice.logging.Loggable;
import com.telran.authenticationservice.persistence.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationBaseService extends AuthenticationAbstractService {

    public AuthenticationBaseService(AccountRepository accountRepository, MailingClient mailingClient) {
        super(accountRepository, mailingClient);
    }

    @Loggable
    @Override
    public JWTBodyReturnDto signIn(AuthenticationDataDto authenticationDataDto) {
        return null;
    }
}
