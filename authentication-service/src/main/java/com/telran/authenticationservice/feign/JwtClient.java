package com.telran.authenticationservice.feign;

import com.telran.authenticationservice.config.FeignConfig;
import com.telran.authenticationservice.dto.GenerateJwtRequest;
import com.telran.authenticationservice.dto.JWTBodyReturnDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "JwtClient", url = "http://jwt-service:8080", path = "/jwt", configuration = FeignConfig.class)
public interface JwtClient {
    @GetMapping("/header")
    String extractTokenFromAuthorizationHeader(@RequestHeader("Authorization") String authHeader);

    @GetMapping("/username/{token}")
    String getUsername(@PathVariable String token);

    @GetMapping("/roles/{token}")
    List<String> getRoles(@PathVariable String token);

    @PostMapping("/generate/accessToken")
    String generateAccessToken(@RequestBody GenerateJwtRequest generateJwtRequest);

    @PostMapping("/generate/refreshToken")
    String generateRefreshToken(@RequestBody String username);

    @PostMapping("/generate/all")
    JWTBodyReturnDto generateAllTokens(@RequestBody GenerateJwtRequest generateJwtRequest);
}
