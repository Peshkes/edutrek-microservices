package com.telran.courseservice.error;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(String message) {
        super("Course not found: " + message);
    }
}
