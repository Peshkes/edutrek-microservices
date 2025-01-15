package com.telran.securityservice.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private static final Logger log = LoggerFactory.getLogger(GatewayController.class);
    @Value("${service.authentication.url}")
    private String authenticationServiceUrl;

    @Value("${service.branches.url}")
    private String branchesServiceUrl;

    @Value("${service.contacts.url}")
    private String contactsServiceUrl;

    @Value("${service.courses.url}")
    private String coursesServiceUrl;

    @Value("${service.groups.url}")
    private String groupsServiceUrl;

    @Value("${service.lecturers.url}")
    private String lecturersServiceUrl;

    @Value("${service.logs.url}")
    private String logsServiceUrl;

    @Value("${service.notifications.url}")
    private String notificationsServiceUrl;

    @Value("${service.payments.url}")
    private String paymentsServiceUrl;

    @Value("${service.statuses.url}")
    private String statusesServiceUrl;

    @Value("${service.students.url}")
    private String studentsServiceUrl;

    private final RestTemplate restTemplate;

    private String getTargetUrl(String requestUri) {
        if (requestUri == null || requestUri.split("/").length < 2) {
            throw new IllegalArgumentException("Invalid URI format: " + requestUri);
        }

        String serviceUrl = switch (requestUri.split("/")[1]) {
            case "auth" -> authenticationServiceUrl;
            case "branches" -> branchesServiceUrl;
            case "contacts" -> contactsServiceUrl;
            case "courses" -> coursesServiceUrl;
            case "groups" -> groupsServiceUrl;
            case "lecturers" -> lecturersServiceUrl;
            case "logs" -> logsServiceUrl;
            case "notifications" -> notificationsServiceUrl;
            case "payments" -> paymentsServiceUrl;
            case "statuses" -> statusesServiceUrl;
            case "students" -> studentsServiceUrl;
            default -> throw new IllegalArgumentException("No matching service found for URI: " + requestUri);
        };

        return serviceUrl + requestUri;
    }

    private HttpHeaders getRequestHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.add(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    @GetMapping("/csrf")
    @ResponseStatus(HttpStatus.OK)
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }

    @RequestMapping(value = "/**")
    public ResponseEntity<?> forwardRequest(HttpServletRequest request, @RequestBody(required = false) String body) {
        String targetUrl;
        try {
            targetUrl = getTargetUrl(request.getRequestURI());
            String query = request.getQueryString();
            if (query != null) targetUrl += "?" + query;
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        HttpHeaders headers = getRequestHeaders(request);
        String requestId = UUID.randomUUID().toString();
        headers.add("X-Request-Id", requestId);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        HttpMethod method;
        try {
            method = HttpMethod.valueOf(request.getMethod());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Invalid HTTP method: " + request.getMethod());
        }

        try {
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, method, entity, String.class);
            log.info("Forwarding {} request {} to: {}", method, requestId, targetUrl);
            return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}