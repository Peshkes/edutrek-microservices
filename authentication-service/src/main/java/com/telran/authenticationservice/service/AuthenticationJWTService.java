package com.telran.authenticationservice.service;

import com.telran.authenticationservice.config.SecurityConfig;
import com.telran.authenticationservice.config.UserConfig;

import com.telran.authenticationservice.error.AuthenticationException.*;
import com.telran.authenticationservice.dto.AuthenticationDataDto;
import com.telran.authenticationservice.dto.AuthenticationResultDto;
import com.telran.authenticationservice.feign.JwtClient;
import com.telran.authenticationservice.feign.MailingClient;
import com.telran.authenticationservice.logging.Loggable;
import com.telran.authenticationservice.persistence.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthenticationJWTService extends AuthenticationAbstractService {

    private final UserConfig userConfig;
    private final JwtClient jwtClient;

    public AuthenticationJWTService(AccountRepository accountRepository, MailingClient mailingClient, UserConfig userConfig, JwtClient jwtClient) {
        super(accountRepository, mailingClient);
        this.jwtClient = jwtClient;
        this.mailingClient = mailingClient;
        this.userConfig = userConfig;
    }

    @Loggable
    @Transactional
    public AuthenticationResultDto signIn(AuthenticationDataDto authenticationDataDto) {
        String username = authenticationDataDto.getLogin();
        UserDetails userDetails = userConfig.loadUserByUsername(username);
        if (SecurityConfig.passwordEncoder().matches(authenticationDataDto.getPassword(), userDetails.getPassword())) {
            try {
                String accessToken = jwtClient.generateAccessToken(userDetails);
                String refreshToken = jwtClient.generateRefreshToken(userDetails);
                return new AuthenticationResultDto(accessToken, refreshToken);
            } catch (Exception e) {
                log.error(e.getMessage());
                return null;
            }
        } else
            throw new WrongPasswordException();
    }

    @Loggable
    public AuthenticationResultDto refreshToken(String refreshToken) {
        if (refreshToken != null) {
            String username = jwtClient.getUsername(refreshToken);
            UserDetails userDetails;
            String accessToken;
            try {
                userDetails = userConfig.loadUserByUsername(username);
                accessToken = jwtClient.generateAccessToken(userDetails);
                refreshToken = jwtClient.generateRefreshToken(userDetails);
                return new AuthenticationResultDto(accessToken, refreshToken);
            } catch (Exception e) {
                throw new UsernameNotFoundException(username);
            }
        } else {
            throw new RefreshTokenNotFoundException();
        }
    }


}
