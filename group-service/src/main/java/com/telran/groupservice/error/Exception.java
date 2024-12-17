package com.telran.groupservice.error;

import static com.telran.groupservice.error.Errors.*;


public class Exception extends RuntimeException {

    public Exception(String message) {
        super(message);
    }

    public static class CourseNotFoundException extends Exception {
        public CourseNotFoundException(String message) {
            super(COURSE_NOT_FOUND + message);
        }
    }

    public static class GroupNotFoundException extends Exception {
        public GroupNotFoundException(String message) {
            super(GROUP_NOT_FOUND + message);
        }
    }

    public static class LecturerNotFoundException extends Exception {
        public LecturerNotFoundException(String message) {
            super(LECTURER_NOT_FOUND + message);
        }
    }

    public static class StudentNotFoundInThisGroupException extends Exception {
        public StudentNotFoundInThisGroupException(String groupId, String studentId) {
            super(String.format(STUDENT_NOT_FOUND_IN_THIS_GROUP, studentId, groupId));
        }
    }

    public static class StudentAlreadyInThisGroupException extends Exception {
        public StudentAlreadyInThisGroupException(String groupId, String studentId) {
            super(String.format(STUDENT_ALREADY_IN_THIS_GROUP, studentId, groupId));
        }
    }

    public static class WeekdayNotFoundException extends Exception {
        public WeekdayNotFoundException(int weekdayId) {
            super(WEEKDAY_NOT_FOUND + weekdayId);
        }
    }
    public static class UnsuccessfulRequest extends Exception {
        public UnsuccessfulRequest(String message) {
            super(RABBIT_REQUEST_FAILED + message);
        }
    }

}
