package com.telran.jwtservice.controller;

import com.telran.jwtservice.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
public class JwtController {
    JwtService jwtService;

    @RequestMapping("/username")
    public String getUsername(String token) {
        return jwtService.getUsername(token);
    }

    @RequestMapping("/roles")
    public List<String> getRoles(String token) {
        return jwtService.getRoles(token);
    }

    @RequestMapping("/accessToken")
    public String getAccessToken(HttpServletRequest request) {
        return jwtService.getAccessToken(request);
    }

    @RequestMapping("/refreshToken")
    public String getRefreshToken(HttpServletRequest request) {
        return jwtService.getRefreshToken(request);
    }

    @RequestMapping("/header")
    public String extractTokenFromAuthorizationHeader(HttpServletRequest request) {
        return jwtService.extractTokenFromAuthorizationHeader(request);
    }

    @RequestMapping("/generate/accessToken")
    public String generateAccessToken(UserDetails userDetails) {
        return jwtService.generateAccessToken(userDetails);
    }

    @RequestMapping("/generate/refreshToken")
    public String generateRefreshToken(UserDetails userDetails) {
        return jwtService.generateRefreshToken(userDetails);
    }
}