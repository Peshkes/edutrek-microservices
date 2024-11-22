package com.telran.logservice.error;

public class LogNotFoundException extends RuntimeException {
    public LogNotFoundException(String message) {
        super("Log not found: " + message);
    }
}