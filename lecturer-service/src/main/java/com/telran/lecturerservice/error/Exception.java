package com.telran.lecturerservice.error;

public class Exception extends RuntimeException {

    public Exception(String message) {
        super("Lecturer not found: " + message);
    }

    public static class UnsuccessfulRequest extends Exception {
        public UnsuccessfulRequest(String message) {
            super("Rabbit request failed: " + message);
        }
    }

}
