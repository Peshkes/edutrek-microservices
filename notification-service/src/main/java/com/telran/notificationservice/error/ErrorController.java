package com.telran.notificationservice.error;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import static com.telran.notificationservice.error.Exceptions.*;


@Slf4j
@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(DatabaseException.class)
    ResponseEntity<String> databaseExceptionHandler(DatabaseException e) {
        return returnResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    ResponseEntity<String> notificationNotFoundException(NotificationNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongEntityTypeException.class)
    ResponseEntity<String> wrongEntityTypeException(WrongEntityTypeException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> returnResponse(String message, HttpStatus status) {
        log.error(message);
        return new ResponseEntity<>(message, status);
    }
}