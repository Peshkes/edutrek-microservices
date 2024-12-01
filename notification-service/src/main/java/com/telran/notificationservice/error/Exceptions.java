package com.telran.notificationservice.error;


import com.telran.notificationservice.persistence.EntityTypes;

import java.util.UUID;

import static com.telran.notificationservice.error.ShareErrors.*;

public class Exceptions extends RuntimeException {

    public Exceptions(String message) {
        super(message);
    }

    public static class NotificationNotFoundException extends Exceptions {
        public NotificationNotFoundException(String message) {
            super(NOTIFICATION_NOT_FOUND + message);
        }
    }

    public static class WrongEntityTypeException extends Exceptions {
        public WrongEntityTypeException(EntityTypes message) {
            super(WRONG_ENTITY_TYPE + message);
        }
    }

    public static class NotificationListIsEmptyException extends Exceptions {
        public NotificationListIsEmptyException(UUID id) {
            super(NOTIFICATION_LIST_EMPTY + id.toString());
        }
    }

}
