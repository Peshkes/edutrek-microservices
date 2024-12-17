package com.telran.studentservice.error;
import com.telran.studentservice.error.Exceptions.*;

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

    @ExceptionHandler(Exceptions.CourseNotFoundException.class)
    ResponseEntity<String> courseNotFoundExceptionHandler(CourseNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DatabaseException.class)
    ResponseEntity<String> databaseExceptionHandler(DatabaseException e) {
        return returnResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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


    @ExceptionHandler(StudentNotFoundException.class)
    ResponseEntity<String> studentNotFoundException(StudentNotFoundException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsuccessfulRequest.class)
    ResponseEntity<String> unsuccessfulRequest(UnsuccessfulRequest e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAStudentException.class)
    ResponseEntity<String> notAStudentException(NotAStudentException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentOrContactAlreadyExistsException.class)
    ResponseEntity<String> studentOrContactAlreadyExistsException(StudentOrContactAlreadyExistsException e) {
        return returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> returnResponse(String message, HttpStatus status) {
        log.error(message);
        return new ResponseEntity<>(message, status);
    }
}