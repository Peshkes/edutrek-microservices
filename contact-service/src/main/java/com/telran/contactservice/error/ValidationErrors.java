package com.telran.contactservice.error;

public class ValidationErrors {
    static final String VALIDATION_ERROR = "Validation error: ";

    // Share

    public static final String COMMENT_SIZE = VALIDATION_ERROR + "Comment must be less than 255 characters";

    public static final String EMAIL_MANDATORY = VALIDATION_ERROR + "Email is mandatory";
    public static final String EMAIL_NOT_EMPTY = VALIDATION_ERROR + "Email cannot be empty";
    public static final String EMAIL_INVALID_FORMAT = VALIDATION_ERROR + "Invalid email format";

    public static final String NAME_MANDATORY = VALIDATION_ERROR + "Name is mandatory";
    public static final String NAME_NOT_EMPTY = VALIDATION_ERROR + "Name cannot be empty";
    public static final String NAME_SIZE = VALIDATION_ERROR + "Name must be between 2 and 50 characters";

    // Authentication

    public static final String LOGIN_MANDATORY = VALIDATION_ERROR + "Login is mandatory";
    public static final String LOGIN_SIZE = VALIDATION_ERROR + "Login must be between 2 and 50 characters";

    public static final String PASSWORD_MANDATORY = VALIDATION_ERROR + "Password is mandatory";
    public static final String PASSWORD_SIZE = VALIDATION_ERROR + "Password must be at least 8 characters long";
    public static final String PASSWORD_INVALID_FORMAT = VALIDATION_ERROR + "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character";

    public static final String ROLES_MANDATORY = VALIDATION_ERROR + "Roles are mandatory";
    public static final String ROLE_NOT_NULL = VALIDATION_ERROR + "Role cannot be null";
    public static final String ROLE_INVALID = VALIDATION_ERROR + "Invalid role";

    // Lecturer

    public static final String PHONE_MANDATORY = VALIDATION_ERROR + "Phone is mandatory";
    public static final String PHONE_NOT_EMPTY = VALIDATION_ERROR + "Phone cannot be empty";
    public static final String PHONE_INVALID_FORMAT = VALIDATION_ERROR + "Invalid phone format";

    // Branch

    public static final String BRANCH_MANDATORY = VALIDATION_ERROR + "Branch is mandatory";
}
