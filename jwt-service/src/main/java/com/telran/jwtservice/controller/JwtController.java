package com.telran.jwtservice.controller;

import com.telran.jwtservice.dto.GenerateJwtRequest;
import com.telran.jwtservice.dto.JWTBodyReturnDto;
import com.telran.jwtservice.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
public class JwtController {
    private final JwtService jwtService;

    @GetMapping("/username/{token}")
    public String getUsername(@PathVariable String token) {
        return jwtService.getUsername(token);
    }

    @GetMapping("/roles")
    public List<String> getRoles(String token) {
        return jwtService.getRoles(token);
    }

    @GetMapping("/accessToken")
    public String getAccessToken(HttpServletRequest request) {
        return jwtService.getAccessToken(request);
    }

    @GetMapping("/refreshToken")
    public String getRefreshToken(HttpServletRequest request) {
        return jwtService.getRefreshToken(request);
    }

    @GetMapping("/header")
    public String extractTokenFromAuthorizationHeader(HttpServletRequest request) {
        return jwtService.extractTokenFromAuthorizationHeader(request);
    }

    @PostMapping("/generate/accessToken")
    public String generateAccessToken(@RequestBody GenerateJwtRequest generateJwtRequest) {
        return jwtService.generateAccessToken(generateJwtRequest.getUsername(), generateJwtRequest.getRoles());
    }

    @PostMapping("/generate/refreshToken")
    public String generateRefreshToken(@RequestBody String username) {
        return jwtService.generateRefreshToken(username);
    }

    @PostMapping("/generate/all")
    public JWTBodyReturnDto generateAll(@RequestBody GenerateJwtRequest generateJwtRequest) {
        String accessToken = jwtService.generateAccessToken(generateJwtRequest.getUsername(), generateJwtRequest.getRoles());
        String refreshToken = jwtService.generateRefreshToken(generateJwtRequest.getUsername());
        return new JWTBodyReturnDto(accessToken, refreshToken);
    }
}