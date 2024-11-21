package com.telran.authenticationservice.service;

import com.telran.authenticationservice.config.SecurityConfig;
import com.telran.authenticationservice.config.UserConfig;

import com.telran.authenticationservice.error.AuthenticationException.*;
import com.telran.authenticationservice.dto.AuthenticationDataDto;
import com.telran.authenticationservice.dto.AuthenticationResultDto;
import com.telran.authenticationservice.logging.Loggable;
import com.telran.authenticationservice.persistence.AccountRepository;
import com.goodquestion.edutrek_server.utility_service.EmailService;
import com.goodquestion.edutrek_server.utility_service.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationJWTService extends AuthenticationAbstractService {

    private final UserConfig userConfig;
    private final JwtService jwtService;

    public AuthenticationJWTService(AccountRepository accountRepository, EmailService emailService, UserConfig userConfig, JwtService jwtService) {
        super(accountRepository, emailService);
        this.jwtService = jwtService;
        this.userConfig = userConfig;
    }

    @Loggable
    @Transactional
    public AuthenticationResultDto signIn(AuthenticationDataDto authenticationDataDto) {
        String username = authenticationDataDto.getLogin();
        UserDetails userDetails = userConfig.loadUserByUsername(username);
        if (SecurityConfig.passwordEncoder().matches(authenticationDataDto.getPassword(), userDetails.getPassword())) {
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            return new AuthenticationResultDto(accessToken, refreshToken);
        } else
            throw new WrongPasswordException();
    }

    @Loggable
    public AuthenticationResultDto refreshToken(String refreshToken) {
        if (refreshToken != null) {
            String username = jwtService.getUsername(refreshToken);
            UserDetails userDetails;
            String accessToken;
            try {
                userDetails = userConfig.loadUserByUsername(username);
                accessToken = jwtService.generateAccessToken(userDetails);
                refreshToken = jwtService.generateRefreshToken(userDetails);
                return new AuthenticationResultDto(accessToken, refreshToken);
            } catch (Exception e) {
                throw new UsernameNotFoundException(username);
            }
        } else {
            throw new RefreshTokenNotFoundException();
        }
    }


}
