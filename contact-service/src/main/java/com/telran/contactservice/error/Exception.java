package com.telran.contactservice.error;

import static com.telran.contactservice.error.Errors.*;


public class Exception extends RuntimeException {

    public Exception(String message) {
        super(message);
    }

    public static class ContactNotFoundException extends Exception {
        public ContactNotFoundException(String message) {
            super(CONTACT_NOT_FOUND + message);
        }
    }

    public static class ContactAlreadyExistsException extends Exception {
        public ContactAlreadyExistsException(String phone, String email) {
            super(CONTACT_ALREADY_EXISTS + (email == null ? phone : email));
        }
    }

    public static class BranchNotFoundException extends Exception {
        public BranchNotFoundException(String message) {
            super(BRANCH_NOT_FOUND + message);
        }
    }

    public static class StatusNotFoundException extends Exception {
        public StatusNotFoundException(int id) {
            super(STATUS_NOT_FOUND + id);
        }
    }

    public static class CourseNotFoundException extends Exception {
        public CourseNotFoundException(String message) {
            super(COURSE_NOT_FOUND + message);
        }
    }



}
