package com.telran.securityservice.config;

import com.telran.securityservice.service.CsrfService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CsrfTokenFilter extends OncePerRequestFilter{

    private final CsrfService csrfService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("/csrf".equals(request.getRequestURI()) && "GET".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenFromCookie = getTokenFromCookies(request);
        String tokenFromHeader = getTokenFromHeader(request);

        if (!csrfService.verifyTokens(tokenFromCookie, tokenFromHeader)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("X-CSRF-TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        return request.getHeader("X-CSRF-TOKEN");
    }
}
