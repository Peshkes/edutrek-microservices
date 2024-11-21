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

    public static class ThisIsStudentException extends Exception {
        public ThisIsStudentException() {
            super(THIS_IS_STUDENT);
        }
    }

    public static class LogNotFoundException extends Exception {
        public LogNotFoundException(String message) {
            super(LOG_NOT_FOUND + message);
        }
    }

    public static class ContactAlreadyArchivedException extends Exception {
        public ContactAlreadyArchivedException(String message) {
            super(CONTACT_ALREADY_EXISTS_IN_ARCHIVE + message);
        }
    }

    public static class ContactNotFoundInArchiveAndCurrentException extends Exception {
        public ContactNotFoundInArchiveAndCurrentException(String message) {
            super(CONTACT_NOT_FOUND_IN_CURRENT_AND_IN_ARCHIVE + message);
        }
    }

}
