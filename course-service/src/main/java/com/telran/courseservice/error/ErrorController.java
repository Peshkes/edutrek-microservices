package com.telran.courseservice.error;

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

    @ExceptionHandler(CourseNotFoundException.class)
    ResponseEntity<String> courseNotFoundExceptionHandler(CourseNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
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

    private ResponseEntity<String> returnResponse(String message, HttpStatus status) {
        log.error(message);
        return new ResponseEntity<>(message, status);
    }
}