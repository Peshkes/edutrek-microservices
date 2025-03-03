package com.telran.lecturerservice.error;

import static com.telran.lecturerservice.error.DatabaseErrors.*;

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

    public static class DatabaseUpdatingException extends DatabaseException {
        public DatabaseUpdatingException(String message) {
            super(UPDATING + message);
        }
    }
}
