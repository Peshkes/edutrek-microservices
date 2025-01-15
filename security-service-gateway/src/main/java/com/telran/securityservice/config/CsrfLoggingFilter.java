//package com.telran.securityservice.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.springframework.security.web.csrf.CsrfToken;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class CsrfLoggingFilter extends OncePerRequestFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(CsrfLoggingFilter.class);
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//
//        if (csrfToken != null) {
//            logger.info("CSRF Token: {}", csrfToken.getToken());
//            logger.info("CSRF Header Name: {}", csrfToken.getHeaderName());
//            logger.info("CSRF Parameter Name: {}", csrfToken.getParameterName());
//        } else {
//            logger.warn("No CSRF Token found for the request.");
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}