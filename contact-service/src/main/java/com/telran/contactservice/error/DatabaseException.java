package com.telran.contactservice.error;

import static com.telran.contactservice.error.DatabaseErrors.*;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }

    public static class DatabaseAddingException extends DatabaseException {
        public DatabaseAddingException(String message) {
            super(ADDING + message);
        }
    }

    public static class DatabaseDeletingException extends DatabaseException {
        public DatabaseDeletingException(String message) {
            super(DELETING + message);
        }
    }
}
