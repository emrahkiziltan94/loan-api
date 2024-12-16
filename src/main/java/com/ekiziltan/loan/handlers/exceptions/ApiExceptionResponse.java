package com.ekiziltan.loan.handlers.exceptions;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiExceptionResponse {
    private String message;
    private int status;
    private long timestamp;
}
