package com.ekiziltan.loan.handlers.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CreditApiExceptionHandler {


    @ExceptionHandler
    public ResponseEntity<ApiExceptionResponse> handleApiException(ApiException e) {
        log.error("ApiException occurred: {}", e.getMessage());
        e.printStackTrace();
        ApiExceptionResponse response = ApiExceptionResponse.builder()
                .message(e.getMessage())
                .status(e.getHttpStatus().value())
                .timestamp(System.currentTimeMillis())
                .build();
        return new ResponseEntity<>(response, e.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ApiExceptionResponse> handleAuthorizationDeniedException(AuthorizationDeniedException exception){
        log.error("AuthorizationDeniedException occurred: {}", exception.getMessage());
        exception.printStackTrace();
        ApiExceptionResponse response = ApiExceptionResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.FORBIDDEN.value())
                .timestamp(System.currentTimeMillis())
                .build();
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler
    public ResponseEntity<ApiExceptionResponse> handleException(Exception e) {
        log.error("Exception occurred: {}", e.getMessage());
        e.printStackTrace();
        ApiExceptionResponse response = ApiExceptionResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(System.currentTimeMillis())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
