package com.telran.contactservice.error;
import com.telran.contactservice.error.Exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ErrorController {



    @ExceptionHandler(CourseNotFoundException.class)
    ResponseEntity<String> courseNotFoundExceptionHandler(CourseNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DatabaseException.class)
    ResponseEntity<String> databaseExceptionHandler(DatabaseException e) {
        return returnResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    private ResponseEntity<String> returnResponse(String message, HttpStatus status) {
        log.error(message);
        return new ResponseEntity<>(message, status);
    }
}