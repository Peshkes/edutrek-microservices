package com.telran.jwtservice.service;

import com.telran.jwtservice.logging.Loggable;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtService {
    private final Key secretKey = generateSecretKey();

    @Value("${jwt.access-token.expiration}")
    private int accessTokenExpirationTime;

    @Value("${jwt.refresh-token.expiration}")
    private int refreshTokenExpirationTime;

    private Key generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate secret key", e);
        }
    }

    @Loggable
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + refreshTokenExpirationTime);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(secretKey)
                .compact();
    }

    @Loggable
    public String generateAccessToken(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + accessTokenExpirationTime);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(secretKey)
                .compact();
    }

    @Loggable
    public String getUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    @Loggable
    public List<String> getRoles(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }

    @Loggable
    public String getRefreshToken(HttpServletRequest request) {
        return getTokenFromCookies("refreshToken", request);
    }

    @Loggable
    public String getAccessToken(HttpServletRequest request) {
        return getTokenFromCookies("accessToken", request);
    }

    private String getTokenFromCookies(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public String extractTokenFromAuthorizationHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) return authHeader.substring(7);
        return null;
    }
}
