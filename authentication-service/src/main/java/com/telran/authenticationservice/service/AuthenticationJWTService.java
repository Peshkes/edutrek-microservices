package com.telran.authenticationservice.service;

import com.telran.authenticationservice.config.SecurityConfig;
import com.telran.authenticationservice.config.UserConfig;

import com.telran.authenticationservice.dto.GenerateJwtRequest;
import com.telran.authenticationservice.dto.JWTBodyReturnDto;
import com.telran.authenticationservice.error.AuthenticationException.*;
import com.telran.authenticationservice.dto.AuthenticationDataDto;
import com.telran.authenticationservice.feign.JwtClient;
import com.telran.authenticationservice.feign.MailingClient;
import com.telran.authenticationservice.logging.Loggable;
import com.telran.authenticationservice.persistence.AccountRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

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
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public JWTBodyReturnDto signIn(AuthenticationDataDto authenticationDataDto) {
        String username = authenticationDataDto.getLogin();
        UserDetails userDetails = userConfig.loadUserByUsername(username);
        if (userDetails == null) throw new UsernameNotFoundException(username);

        if (SecurityConfig.passwordEncoder().matches(authenticationDataDto.getPassword(), userDetails.getPassword())) {
            JWTBodyReturnDto jwtBodyReturnDto = generateAllTokens(userDetails, username);
            if (jwtBodyReturnDto == null || jwtBodyReturnDto.getAccessToken() == null || jwtBodyReturnDto.getRefreshToken() == null)
                throw new TokenIsNull();
            else
                return jwtBodyReturnDto;
        } else
            throw new WrongPasswordException();
    }

    @Loggable
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public JWTBodyReturnDto refreshToken(String refreshToken) {
        if (refreshToken != null) {
            String username = jwtClient.getUsername(refreshToken);
            UserDetails userDetails;
            try {
                userDetails = userConfig.loadUserByUsername(username);
            } catch (Exception e) {throw new UsernameNotFoundException(username);}
            if (userDetails == null) throw new UsernameNotFoundException(username);

            JWTBodyReturnDto jwtBodyReturnDto = generateAllTokens(userDetails, username);
            if (jwtBodyReturnDto == null || jwtBodyReturnDto.getAccessToken() == null || jwtBodyReturnDto.getRefreshToken() == null)
                throw new TokenIsNull();
            else
                return jwtBodyReturnDto;

        } else {
            throw new RefreshTokenNotFoundException();
        }
    }

    private JWTBodyReturnDto generateAllTokens(UserDetails userDetails, String username) {
        List<String> roleList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        return jwtClient.generateAllTokens(new GenerateJwtRequest(username, roleList));
    }


}
