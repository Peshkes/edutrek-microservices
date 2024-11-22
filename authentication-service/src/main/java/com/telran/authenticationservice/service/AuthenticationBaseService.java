package com.telran.authenticationservice.service;

import com.goodquestion.edutrek_server.utility_service.EmailService;
import com.telran.authenticationservice.dto.AuthenticationDataDto;
import com.telran.authenticationservice.dto.AuthenticationResultDto;
import com.telran.authenticationservice.logging.Loggable;
import com.telran.authenticationservice.persistence.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationBaseService extends AuthenticationAbstractService {

    public AuthenticationBaseService(AccountRepository accountRepository, EmailService emailService) {
        super(accountRepository, emailService);
    }

    @Loggable
    @Override
    public AuthenticationResultDto signIn(AuthenticationDataDto authenticationDataDto) {
        return null;
    }
}
