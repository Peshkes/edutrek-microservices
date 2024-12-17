package com.telran.studentservice.error;


import static com.telran.studentservice.error.Errors.*;

public class Exceptions extends RuntimeException {

    public Exceptions(String message) {
        super(message);
    }

    public static class BranchNotFoundException extends Exceptions {
        public BranchNotFoundException(String message) {
            super(BRANCH_NOT_FOUND + message);
        }
    }

    public static class StatusNotFoundException extends Exceptions {
        public StatusNotFoundException(int id) {
            super(STATUS_NOT_FOUND + id);
        }
    }

    public static class CourseNotFoundException extends Exceptions {
        public CourseNotFoundException(String message) {
            super(COURSE_NOT_FOUND + message);
        }
    }

    public static class StudentNotFoundException extends Exceptions {
        public StudentNotFoundException(String message) {
            super(STUDENT_NOT_FOUND + message);
        }
    }

    public static class UnsuccessfulRequest extends Exceptions {
        public UnsuccessfulRequest(String message) {
            super(RABBIT_REQUEST_FAILED + message);
        }
    }

    public static class NotAStudentException extends Exceptions {
        public NotAStudentException() {
            super(NOT_A_STUDENT_STATUS);
        }
    }

    public static class StudentOrContactAlreadyExistsException extends Exceptions {
        public StudentOrContactAlreadyExistsException() {
            super(STUDENT_OR_CONTACT_ALREADY_EXISTS);
        }
    }
}
