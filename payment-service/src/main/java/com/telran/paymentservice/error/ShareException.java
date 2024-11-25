package com.telran.paymentservice.error;


import static com.telran.paymentservice.error.ShareErrors.*;

public class ShareException extends RuntimeException {

    public ShareException(String message) {
        super(message);
    }


    public static class StatusNotFoundException extends ShareException {
        public StatusNotFoundException(int id) {
            super(STATUS_NOT_FOUND + id);
        }
    }

    public static class StudentNotFoundException extends ShareException {
        public StudentNotFoundException(String message) {
            super(STUDENT_NOT_FOUND + message);
        }
    }

    public static class PaymentInfoNotFoundException extends ShareException {
        public PaymentInfoNotFoundException(String message) {
            super(PAYMENT_INFORMATION_NOT_FOUND + message);
        }
    }
}
