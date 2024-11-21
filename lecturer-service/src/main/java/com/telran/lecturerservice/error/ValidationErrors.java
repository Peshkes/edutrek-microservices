package com.telran.lecturerservice.error;

public class ValidationErrors {
    static final String VALIDATION_ERROR = "Validation error: ";

    public static final String COMMENT_SIZE = VALIDATION_ERROR + "Comment must be less than 255 characters";

    public static final String EMAIL_MANDATORY = VALIDATION_ERROR + "Email is mandatory";
    public static final String EMAIL_NOT_EMPTY = VALIDATION_ERROR + "Email cannot be empty";
    public static final String EMAIL_INVALID_FORMAT = VALIDATION_ERROR + "Invalid email format";

    public static final String NAME_MANDATORY = VALIDATION_ERROR + "Name is mandatory";
    public static final String NAME_NOT_EMPTY = VALIDATION_ERROR + "Name cannot be empty";
    public static final String NAME_SIZE = VALIDATION_ERROR + "Name must be between 2 and 50 characters";\

    public static final String PHONE_MANDATORY = VALIDATION_ERROR + "Phone is mandatory";
    public static final String PHONE_NOT_EMPTY = VALIDATION_ERROR + "Phone cannot be empty";
    public static final String PHONE_INVALID_FORMAT = VALIDATION_ERROR + "Invalid phone format";

    public static final String BRANCH_MANDATORY = VALIDATION_ERROR + "Branch is mandatory";
}
