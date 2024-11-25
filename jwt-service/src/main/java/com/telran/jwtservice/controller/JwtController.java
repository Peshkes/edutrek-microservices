package com.telran.jwtservice.controller;

import com.telran.jwtservice.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
public class JwtController {
    JwtService jwtService;

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
    public String generateAccessToken(@RequestBody UserDetails userDetails) {
        return jwtService.generateAccessToken(userDetails);
    }

    @PostMapping("/generate/refreshToken")
    public String generateRefreshToken(@RequestBody UserDetails userDetails) {
        return jwtService.generateRefreshToken(userDetails);
    }
}