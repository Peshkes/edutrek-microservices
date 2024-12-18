package com.telran.lecturerservice.error;

public class LecturerNotFoundException extends RuntimeException {
    public LecturerNotFoundException(String message) {
        super("Lecturer not found: " + message);
    }
}

