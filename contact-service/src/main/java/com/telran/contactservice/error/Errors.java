package com.telran.contactservice.error;

public class Errors {
    static final String CONTACT_NOT_FOUND = "Contact not found: ";
    static final String CONTACT_ALREADY_EXISTS = "Contact already exists: ";
    static final String CONTACT_ALREADY_EXISTS_IN_ARCHIVE = "Contact already exists in the archive: ";
    static final String THIS_IS_STUDENT = "It seems that this is a student, not a contact, so you won't be able to delete it. " +
            "You'll have to delete the student. But you don't have to worry about it because the program has already done it for you :)";
}
