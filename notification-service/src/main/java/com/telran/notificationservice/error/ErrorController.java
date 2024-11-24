package com.telran.notificationservice.error;


import jakarta.validation.ConstraintViolationException;
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

    @ExceptionHandler(DatabaseException.class)
    ResponseEntity<String> databaseExceptionHandler(DatabaseException e) {
        return returnResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ShareException.NotificationNotFoundException.class)
    ResponseEntity<String> notificationNotFoundException(ShareException.NotificationNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> returnResponse(String message, HttpStatus status) {
        log.error(message);
        return new ResponseEntity<>(message, status);
    }
}