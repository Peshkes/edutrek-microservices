package com.telran.securityservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "JwtClient", url = "http://jwt-service:8080", path = "/jwt")
public interface JwtClient {
    @GetMapping("/header")
    String extractTokenFromAuthorizationHeader(@RequestHeader("Authorization") String authHeader);

    @GetMapping("/username/{token}")
    String getUsername(@PathVariable String token);

    @GetMapping("/roles/{token}")
    List<String> getRoles(@PathVariable String token);
}
