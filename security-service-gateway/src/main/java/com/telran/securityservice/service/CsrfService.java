package com.telran.securityservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CsrfService {

    private static final Logger log = LoggerFactory.getLogger(CsrfService.class);
    private final SecretKey secretKey;

    public CsrfService() {
        this.secretKey = generateSecretKey();
    }

    private SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Error generating the key", e);
        }
    }

    public String generateToken() {
        try {
            String sessionId = UUID.randomUUID().toString();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(sessionId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating the token", e);
        }
    }

    public boolean verifyTokens(String tokenFromCookie, String tokenFromHeader) {
        try {
            String sessionIdFromCookie = decryptToken(tokenFromCookie);
            String sessionIdFromHeader = decryptToken(tokenFromHeader);

            if (sessionIdFromCookie.equals(sessionIdFromHeader)) {
                logSession(sessionIdFromCookie);
                return true;
            } else {
                logInvalidCsrf("Payloads do not match", tokenFromCookie, tokenFromHeader);
                return false;
            }
        } catch (Exception e) {
            logInvalidCsrf("Token verification error: " + e.getMessage(), tokenFromCookie, tokenFromHeader);
            return false;
        }
    }

    private String decryptToken(String token) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(token));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting the token", e);
        }
    }

    private void logSession(String sessionId) {
        log.info("CSRF token is valid. Request from session: {}", sessionId);
    }

    private void logInvalidCsrf(String reason, String cookieToken, String headerToken) {
        log.error("CSRF error: {}", reason);
        log.error("Token from cookie: {}", cookieToken);
        log.error("Token from header: {}", headerToken);
    }
}