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
}
