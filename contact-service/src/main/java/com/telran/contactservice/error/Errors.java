package com.telran.contactservice.error;

public class Errors {
    static final String CONTACT_NOT_FOUND = "Contact not found: ";
    static final String CONTACT_ALREADY_EXISTS = "Contact already exists: ";
    static final String THIS_IS_STUDENT = "It seems that this is a student, not a contact, so you won't be able to delete it. " +
            "You'll have to delete the student. But you don't have to worry about it because the program has already done it for you :)";
    static final String BRANCH_NOT_FOUND = "Branch not found: ";
    static final String STATUS_NOT_FOUND = "Status with id was not found: ";
    static final String COURSE_NOT_FOUND = "Course not found: ";
    static final String  RABBIT_REQUEST_FAILED = "Rabbit request failed: ";
    static final String  STUDENT_OR_CONTACT_ALREADY_EXISTS = "Student or contact already exists";
    static final String  STUDENT_ALREADY_EXISTS = "Student already exists: ";
    static final String  PROMOTE_USUCCESFULL = "Contact wasn't promoted: ";
}
