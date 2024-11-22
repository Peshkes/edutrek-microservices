package com.telran.securityservice.config;

import com.telran.securityservice.dto.AccountDocumentDto;
import com.telran.securityservice.feign.AuthenticationClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class ExpiredPasswordFilter extends OncePerRequestFilter {

    private final AuthenticationClient client;

    @Value("${password.activation.period}")
    private int activationPeriod;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (checkEndpoint(request.getMethod(), request.getServletPath()) && auth != null) {
            String name = auth.getName();
            AccountDocumentDto accountDocument = client.findByLogin(name);
            if (accountDocument != null) {
                if (ChronoUnit.DAYS.between(accountDocument.getLastPasswordChange(), LocalDate.now()) >= activationPeriod) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Your password expired");
                }
            } else
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Account wasn't found");
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        return !(method.equals("PUT") && servletPath.equals("/auth/password/{id}"));
    }
}
