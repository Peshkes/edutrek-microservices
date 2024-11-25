package com.telran.notificationservice.error;


import java.util.UUID;

import static com.telran.notificationservice.error.ShareErrors.*;

public class ShareException extends RuntimeException {

    public ShareException(String message) {
        super(message);
    }

    public static class NotificationNotFoundException extends ShareException {
        public NotificationNotFoundException(String message) {
            super(NOTIFICATION_NOT_FOUND + message);
        }
    }

    public static class NotificationListIsEmptyException extends ShareException {
        public NotificationListIsEmptyException(UUID id) {
            super(NOTIFICATION_LIST_EMPTY + id.toString());
        }
    }

}
