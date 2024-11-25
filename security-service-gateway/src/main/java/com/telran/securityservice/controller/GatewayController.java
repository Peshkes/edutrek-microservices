package com.telran.securityservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GatewayController {

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

    @RequestMapping(value = "/**")
    public ResponseEntity<?> forwardRequest(HttpServletRequest request, @RequestBody(required = false) String body) {
        String targetUrl;
        try {
            targetUrl = getTargetUrl(request.getRequestURI());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        HttpHeaders headers = getRequestHeaders(request);
        headers.add("X-Request-Id", UUID.randomUUID().toString());
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        HttpMethod method;
        try {
            method = HttpMethod.valueOf(request.getMethod());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Invalid HTTP method: " + request.getMethod());
        }

        try {
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, method, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while forwarding the request: " + e.getMessage());
        }
    }
}