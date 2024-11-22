package com.telran.authenticationservice.controller;

import com.telran.authenticationservice.dto.*;
import com.telran.authenticationservice.feign.JwtClient;
import com.telran.authenticationservice.persistence.AccountDocument;
import com.telran.authenticationservice.service.AuthenticationJWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final JwtClient jwtClient;
    private final AuthenticationJWTService authenticationService;
//    private final AuthenticationBaseService authenticationService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<PublicAccountDataDto> getAllAccounts() {
        return authenticationService.getAllAccounts();
    }

    @GetMapping("/csrf")
    @ResponseStatus(HttpStatus.OK)
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
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

    @PostMapping("")
//    public ResponseEntity<String> signIn(@Valid @RequestBody AuthenticationDataDto authenticationDataDto, HttpServletResponse response) {
    public ResponseEntity<JWTBodyReturnDto> signIn(@Valid @RequestBody AuthenticationDataDto authenticationDataDto, HttpServletResponse response) {

        AuthenticationResultDto result = authenticationService.signIn(authenticationDataDto);
//        response.addCookie(createCookie("accessToken", result.getAccessToken()));
//        response.addCookie(createCookie("refreshToken", result.getRefreshToken()));

//        return ResponseEntity.ok("Sign-in successful");
        return ResponseEntity.ok(new JWTBodyReturnDto(result.getAccessToken(), result.getRefreshToken()));
    }

    @PostMapping("/refresh")
//    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    public ResponseEntity<JWTBodyReturnDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
//        String refreshToken = jwtService.getRefreshToken(request);
        String authHeader = request.getHeader("Authorization");
        String refreshToken = jwtClient.extractTokenFromAuthorizationHeader(authHeader);
        AuthenticationResultDto result = authenticationService.refreshToken(refreshToken);
//        response.addCookie(createCookie("accessToken", result.getAccessToken()));
//        response.addCookie(createCookie("refreshToken", result.getRefreshToken()));
        //  return ResponseEntity.ok("Refresh successful");
        return ResponseEntity.ok(new JWTBodyReturnDto(result.getAccessToken(), result.getRefreshToken()));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(HttpServletResponse response) {

        response.addCookie(createCookie("accessToken", null));
        response.addCookie(createCookie("refreshToken", null));

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
        cookie.setSecure(true);
        return cookie;
    }
}
