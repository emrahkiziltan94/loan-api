package com.ekiziltan.loan.handlers.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;

import static org.junit.jupiter.api.Assertions.*;

class CreditApiExceptionHandlerTest {

    private CreditApiExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new CreditApiExceptionHandler();
    }

    @Test
    void handleApiException_ShouldReturnProperResponse() {
        // Arrange
        String errorMessage = "API exception occurred";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(errorMessage, status);

        // Act
        ResponseEntity<ApiExceptionResponse> responseEntity = exceptionHandler.handleApiException(apiException);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(status, responseEntity.getStatusCode());

        ApiExceptionResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(errorMessage, responseBody.getMessage());
        assertEquals(status.value(), responseBody.getStatus());
        assertTrue(responseBody.getTimestamp() > 0);
    }

    @Test
    void handleAuthorizationDeniedException_ShouldReturnForbiddenResponse() {
        // Arrange
        String errorMessage = "Authorization denied";
        AuthorizationDeniedException authException = new AuthorizationDeniedException(errorMessage);

        // Act
        ResponseEntity<ApiExceptionResponse> responseEntity = exceptionHandler.handleAuthorizationDeniedException(authException);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());

        ApiExceptionResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(errorMessage, responseBody.getMessage());
        assertEquals(HttpStatus.FORBIDDEN.value(), responseBody.getStatus());
        assertTrue(responseBody.getTimestamp() > 0);
    }

    @Test
    void handleException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        String errorMessage = "Generic exception occurred";
        Exception exception = new Exception(errorMessage);

        // Act
        ResponseEntity<ApiExceptionResponse> responseEntity = exceptionHandler.handleException(exception);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiExceptionResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(errorMessage, responseBody.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseBody.getStatus());
        assertTrue(responseBody.getTimestamp() > 0);
    }
}

