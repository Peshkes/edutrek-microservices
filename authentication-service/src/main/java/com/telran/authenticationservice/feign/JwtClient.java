package com.telran.authenticationservice.feign;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "JwtClient", url = "https://jsonplaceholder.typicode.com")
public interface JwtClient {
    @GetMapping("/jwt/header")
    String extractTokenFromAuthorizationHeader(@RequestHeader("Authorization") String authHeader);

    @GetMapping("/jwt/username/{token}")
    String getUsername(@PathVariable String token);

    @GetMapping("/jwt/roles/{token}")
    List<String> getRoles(@PathVariable String token);

    @GetMapping("/jwt/accessToken")
    String generateAccessToken(UserDetails userDetails);

    @GetMapping("/jwt/refreshToken")
    String generateRefreshToken(UserDetails userDetails);
}
