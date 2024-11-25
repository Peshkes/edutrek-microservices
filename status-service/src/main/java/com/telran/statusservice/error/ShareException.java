package com.telran.statusservice.error;


import static com.telran.statusservice.error.ShareErrors.*;

public class ShareException extends RuntimeException {

    public ShareException(String message) {
        super(message);
    }

    public static class LecturerNotFoundException extends ShareException {
        public LecturerNotFoundException(String message) {
            super(LECTURER_NOT_FOUND + message);
        }
    }

    public static class StudentNotFoundInThisGroupException extends ShareException {
        public StudentNotFoundInThisGroupException(String groupId, String studentId) {
            super(String.format(STUDENT_NOT_FOUND_IN_THIS_GROUP, studentId, groupId));
        }
    }

    public static class StudentAlreadyInThisGroupException extends ShareException {
        public StudentAlreadyInThisGroupException(String groupId, String studentId) {
            super(String.format(STUDENT_ALREADY_IN_THIS_GROUP, studentId, groupId));
        }
    }

    public static class StatusNotFoundException extends ShareException {
        public StatusNotFoundException(int id) {
            super(STATUS_NOT_FOUND + id);
        }
    }

}
