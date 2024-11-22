package com.telran.contactservice.error;

import com.goodquestion.edutrek_server.error.AuthenticationException.*;

import static com.goodquestion.edutrek_server.error.ShareException.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(ExpiredJwtException.class)
    ResponseEntity<String> expiredJwtExceptionHandler(ExpiredJwtException e) {
        return returnResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    ResponseEntity<String> malformedJwtExceptionHandler(MalformedJwtException e) {
        return returnResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(LogNotFoundException.class)
    ResponseEntity<String> logNotFoundExceptionHandler(LogNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StudentAlreadyInThisGroupException.class)
    ResponseEntity<String> studentAlreadyInThisGroupExceptionHandler(StudentAlreadyInThisGroupException e) {
        return returnResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StudentNotFoundInThisGroupException.class)
    ResponseEntity<String> studentNotFoundInThisGroupExceptionHandler(StudentNotFoundInThisGroupException e) {
        return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LecturerNotFoundException.class)
    ResponseEntity<String> lecturerNotFoundExceptionHandler(LecturerNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    ResponseEntity<String> refreshTokenNotFoundExceptionHandler(RefreshTokenNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GroupNotFoundException.class)
    ResponseEntity<String> groupNotFoundExceptionHandler(GroupNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    ResponseEntity<String> courseNotFoundExceptionHandler(CourseNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordAlreadyUsedException.class)
    ResponseEntity<String> passwordAlreadyUsedExceptionHandler(PasswordAlreadyUsedException e) {
        return returnResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RoleExistsException.class)
    ResponseEntity<String> passwordAlreadyUsedExceptionHandler(RoleExistsException e) {
        return returnResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RoleNotExistsException.class)
    ResponseEntity<String> passwordAlreadyUsedExceptionHandler(RoleNotExistsException e) {
        return returnResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(LoginAlreadyExistsException.class)
    ResponseEntity<String> loginAlreadyExistsExceptionHandler(LoginAlreadyExistsException e) {
        return returnResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DatabaseException.class)
    ResponseEntity<String> databaseExceptionHandler(DatabaseException e) {
        return returnResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoAccountsException.class)
    ResponseEntity<String> noAccountsExceptionHandler(NoAccountsException e) {
        return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<String> authenticationExceptionHandler(AuthenticationException e) {
        return returnResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<String> userNotFoundExceptionHandler(UserNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    ResponseEntity<String> userAlreadyExistsExceptionHandler(UserAlreadyExistsException e) {
        return returnResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    ResponseEntity<String> usernameNotFoundExceptionHandler(UsernameNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(WrongPasswordException.class)
    ResponseEntity<String> wrongPasswordExceptionHandler(WrongPasswordException e) {
        return returnResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return returnResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<String> handlerMethodValidationExceptionHandler(HandlerMethodValidationException e) {
        String message = e.getAllErrors().stream().map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return returnResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<String> methodArgumentTypeMismatchExceptionHandler() {
        return returnResponse("Invalid argument type. Please check the provided data.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<String> httpMessageNotReadableExceptionHandler() {
        return returnResponse("Error reading JSON. Please check the data format.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> illegalArgumentException(IllegalArgumentException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<String> constraintViolationException(ConstraintViolationException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StatusNotFoundException.class)
    ResponseEntity<String> statusNotFoundException(StatusNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BranchNotFoundException.class)
    ResponseEntity<String> branchNotFoundException(BranchNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ContactNotFoundException.class)
    ResponseEntity<String> contactNotFoundException(ContactNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ContactAlreadyExistsException.class)
    ResponseEntity<String> contactAlreadyExistsException(ContactAlreadyExistsException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ContactAlreadyArchivedException.class)
    ResponseEntity<String> contactAlreadyArchivedException(ContactAlreadyArchivedException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    ResponseEntity<String> studentNotFoundException(StudentNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentAlreadyExistsException.class)
    ResponseEntity<String> studentAlreadyExistsException(StudentAlreadyExistsException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentInfoNotFoundException.class)
    ResponseEntity<String> paymentInfoNotFoundException(PaymentInfoNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ContactNotFoundInArchiveAndCurrentException.class)
    ResponseEntity<String> contactNotFoundInArchiveAndCurrentException(ContactNotFoundInArchiveAndCurrentException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    ResponseEntity<String> notificationNotFoundException(NotificationNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> returnResponse(String message, HttpStatus status) {
        log.error(message);
        return new ResponseEntity<>(message, status);
    }
}