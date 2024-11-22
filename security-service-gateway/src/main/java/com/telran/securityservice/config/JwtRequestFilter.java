package com.telran.securityservice.config;

import com.telran.securityservice.feign.JwtClient;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtClient jwtClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (checkEndpoint(request.getMethod(), request.getServletPath())) {
//            String accessToken = jwtService.getAccessToken(request);
            String authorizationHeader = request.getHeader("Authorization");
            String accessToken = jwtClient.extractTokenFromAuthorizationHeader(authorizationHeader);

            String username = null;
            if (accessToken != null) {
                try {
                    username = jwtClient.getUsername(accessToken);
                } catch (ExpiredJwtException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token has expired.");
                    return;
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: " + e.getMessage());
                    return;
                }
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        jwtClient.getRoles(accessToken)
                                .stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        boolean isRefresh = method.equals("POST") && servletPath.equals("/auth/refresh");
        boolean isSignIn = method.equals("POST") && servletPath.equals("/auth");
//        boolean isGetNotification = method.equals("GET") && servletPath.equals("/sse/subscribe/{clientId}");
        return !(isSignIn || isRefresh );
    }


}