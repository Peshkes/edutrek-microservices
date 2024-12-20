package com.telran.statusservice.error;

import com.telran.statusservice.error.ShareException.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ErrorController {

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

    @ExceptionHandler(DatabaseException.class)
    ResponseEntity<String> databaseExceptionHandler(DatabaseException e) {
        return returnResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> illegalArgumentException(IllegalArgumentException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> returnResponse(String message, HttpStatus status) {
        log.error(message);
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(StatusNotFoundException.class)
    ResponseEntity<String> statusNotFoundException(StatusNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StatusNameNotFoundException.class)
    ResponseEntity<String> statusNameNotFoundException(StatusNameNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}