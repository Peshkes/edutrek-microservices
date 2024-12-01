package com.telran.lecturerservice.error;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException(String message) {
        super("Branch not found: " + message);
    }
}

