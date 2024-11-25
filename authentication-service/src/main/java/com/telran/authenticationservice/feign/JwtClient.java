package com.telran.authenticationservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "JwtClient", url = "http://jwt-service:8080")
public interface JwtClient {
    @GetMapping("/jwt/header")
    String extractTokenFromAuthorizationHeader(@RequestHeader("Authorization") String authHeader);

    @GetMapping("/jwt/username/{token}")
    String getUsername(@PathVariable String token);

    @GetMapping("/jwt/roles/{token}")
    List<String> getRoles(@PathVariable String token);

    @PostMapping("/jwt/accessToken")
    String generateAccessToken(@RequestBody UserDetails userDetails);

    @PostMapping("/jwt/refreshToken")
    String generateRefreshToken(@RequestBody UserDetails userDetails);
}
