package com.telran.authenticationservice.controller;

import com.telran.authenticationservice.dto.*;
import com.telran.authenticationservice.persistence.AccountDocument;
import com.telran.authenticationservice.service.AuthenticationJWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

//    private final JwtClient jwtClient;
    private final AuthenticationJWTService authenticationService;
//    private final AuthenticationBaseService authenticationService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<PublicAccountDataDto> getAllAccounts() {
        return authenticationService.getAllAccounts();
    }

    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PublicAccountDataDto getAccountById(@PathVariable @UUID String id) {
        return authenticationService.getAccountById(java.util.UUID.fromString(id));
    }

    @GetMapping("/login/{login}")
    @ResponseStatus(HttpStatus.OK)
    public PublicAccountDataDto getAccountByLogin(@PathVariable String login) {
        return authenticationService.getAccountByLogin(login);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("")
    public ResponseEntity<String> signIn(@Valid @RequestBody AuthenticationDataDto authenticationDataDto, HttpServletResponse response) {
//    public JWTBodyReturnDto signIn(@Valid @RequestBody AuthenticationDataDto authenticationDataDto, HttpServletResponse response) {
//        return authenticationService.signIn(authenticationDataDto);
        JWTBodyReturnDto result = authenticationService.signIn(authenticationDataDto);
        response.addCookie(createCookie("accessToken", result.getAccessToken()));
        response.addCookie(createCookie("refreshToken", result.getRefreshToken()));

        return ResponseEntity.ok("Sign-in successful");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
//    public JWTBodyReturnDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getTokenFromCookies(request);
//        String authHeader = request.getHeader("Authorization");
//        String refreshToken = jwtClient.extractTokenFromAuthorizationHeader(authHeader);
//        return authenticationService.refreshToken(refreshToken);
        JWTBodyReturnDto result = authenticationService.refreshToken(refreshToken);
        response.addCookie(createCookie("accessToken", result.getAccessToken()));
        response.addCookie(createCookie("refreshToken", result.getRefreshToken()));
          return ResponseEntity.ok("Refresh successful");
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(HttpServletResponse response) {

        response.addCookie(createCookie("accessToken", null, 0));
        response.addCookie(createCookie("refreshToken", null, 0));
        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/account")
    public ResponseEntity<String> addNewAccount(@Valid @RequestBody AddNewAccountRequestDto addNewAccountRequestDto) {
        authenticationService.addNewAccount(addNewAccountRequestDto);
        return new ResponseEntity<>("Account created", HttpStatus.CREATED);
    }

    @PostMapping("/rollback")
    public ResponseEntity<String> rollback(@RequestBody AccountDocument accountDocument) {
        String name = authenticationService.rollback(accountDocument);
        return new ResponseEntity<>("Account " + name + " rolled back", HttpStatus.CREATED);
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<String> changePassword(@PathVariable @UUID String id, @Valid @RequestBody ChangePasswordRequestDto changePasswordRequest) {
        authenticationService.changePassword(java.util.UUID.fromString(id), changePasswordRequest);
        return new ResponseEntity<>("Password changed", HttpStatus.OK);
    }

    @PutMapping("/login/{id}")
    public ResponseEntity<String> changeLogin(@PathVariable @UUID String id, @Valid @RequestBody ChangeLoginRequestDto changeLoginRequest) {
        authenticationService.changeLogin(java.util.UUID.fromString(id), changeLoginRequest);
        return new ResponseEntity<>("Login changed", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDocument deleteAccount(@PathVariable @UUID String id) {
        return authenticationService.deleteAccount(java.util.UUID.fromString(id));
    }

    //UTILS
    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false);
        return cookie;
    }

    private Cookie createCookie(String name, String value, int age) {
        Cookie cookie = createCookie(name, value);
        cookie.setMaxAge(age);
        return cookie;
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
