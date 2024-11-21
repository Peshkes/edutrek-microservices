package com.telran.authenticationservice.error;

public class AuthenticationErrors {
    static final String USER_NOT_FOUND = "User not found with id: ";
    static final String USER_ALREADY_EXISTS = "User already exists with username: ";
    static final String USERNAME_NOT_FOUND = "User not found with username: ";
    static final String EMAIL_ALREADY_EXISTS = "User already exists with email: ";
    static final String WRONG_PASSWORD = "Wrong password. Please try again.";
    static final String NO_ACCOUNTS = "No accounts found.";
    static final String LOGIN_ALREADY_EXISTS = "Account already exists with login: ";
    static final String PASSWORD_ALREADY_USED = "Password already used. Please try again.";
    static final String ROLE_EXISTS = "Role already exists: ";
    static final String ROLE_NOT_EXISTS = "Role doesn't exist: ";
    static final String REFRESH_TOKEN_NOT_FOUND = "Refresh token not found.";
}
