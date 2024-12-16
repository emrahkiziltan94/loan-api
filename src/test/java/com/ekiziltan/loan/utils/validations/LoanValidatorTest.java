package com.ekiziltan.loan.utils.validations;

import com.ekiziltan.loan.dto.CreateLoanRequest;
import com.ekiziltan.loan.entity.Customer;
import com.ekiziltan.loan.handlers.exceptions.ApiException;
import com.ekiziltan.loan.validations.LoanValidator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanValidatorTest {

    @Test
    void testValidateLoanInstallmentsValid() {
        assertDoesNotThrow(() -> LoanValidator.validateLoanInstallments(6));
        assertDoesNotThrow(() -> LoanValidator.validateLoanInstallments(9));
        assertDoesNotThrow(() -> LoanValidator.validateLoanInstallments(12));
        assertDoesNotThrow(() -> LoanValidator.validateLoanInstallments(24));
    }

    @Test
    void testValidateLoanInstallmentsInvalid() {
        ApiException exception = assertThrows(ApiException.class, () -> LoanValidator.validateLoanInstallments(5));
        assertEquals("Installment count must be one of 6, 9, 12, 24", exception.getMessage());
        

        exception = assertThrows(ApiException.class, () -> LoanValidator.validateLoanInstallments(30));
        assertEquals("Installment count must be one of 6, 9, 12, 24", exception.getMessage());
        
    }

    @Test
    void testValidateInterestRateValid() {
        assertDoesNotThrow(() -> LoanValidator.validateInterestRate(0.1));
        assertDoesNotThrow(() -> LoanValidator.validateInterestRate(0.3));
        assertDoesNotThrow(() -> LoanValidator.validateInterestRate(0.5));
    }

    @Test
    void testValidateInterestRateInvalid() {
        ApiException exception = assertThrows(ApiException.class, () -> LoanValidator.validateInterestRate(0.05));
        assertEquals("Interest rate must be between 0.1 and 0.5", exception.getMessage());
        

        exception = assertThrows(ApiException.class, () -> LoanValidator.validateInterestRate(0.6));
        assertEquals("Interest rate must be between 0.1 and 0.5", exception.getMessage());
        
    }

    @Test
    void testValidatePrincipalAmountValid() {
        assertDoesNotThrow(() -> LoanValidator.validatePrincipalAmount(new BigDecimal("1000")));
        assertDoesNotThrow(() -> LoanValidator.validatePrincipalAmount(new BigDecimal("0.01")));
    }

    @Test
    void testValidatePrincipalAmountInvalid() {
        ApiException exception = assertThrows(ApiException.class, () -> LoanValidator.validatePrincipalAmount(BigDecimal.ZERO));
        assertEquals("Principal amount must be greater than 0", exception.getMessage());
        

        exception = assertThrows(ApiException.class, () -> LoanValidator.validatePrincipalAmount(new BigDecimal("-100")));
        assertEquals("Principal amount must be greater than 0", exception.getMessage());
        
    }

    @Test
    void testValidateCreateLoanRequestValid() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setNumberOfInstallment(12);
        request.setInterestRate(0.3);
        request.setPrincipalAmount(new BigDecimal("5000"));

        assertDoesNotThrow(() -> LoanValidator.validateCreateLoanRequest(request));
    }

    @Test
    void testValidateCreateLoanRequestInvalidInstallments() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setNumberOfInstallment(7);
        request.setInterestRate(0.3);
        request.setPrincipalAmount(new BigDecimal("5000"));

        ApiException exception = assertThrows(ApiException.class, () -> LoanValidator.validateCreateLoanRequest(request));
        assertEquals("Installment count must be one of 6, 9, 12, 24", exception.getMessage());
        
    }

    @Test
    void testValidateCreateLoanRequestInvalidInterestRate() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setNumberOfInstallment(12);
        request.setInterestRate(0.6);
        request.setPrincipalAmount(new BigDecimal("5000"));

        ApiException exception = assertThrows(ApiException.class, () -> LoanValidator.validateCreateLoanRequest(request));
        assertEquals("Interest rate must be between 0.1 and 0.5", exception.getMessage());
        
    }

    @Test
    void testValidateCreateLoanRequestInvalidPrincipalAmount() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setNumberOfInstallment(12);
        request.setInterestRate(0.3);
        request.setPrincipalAmount(new BigDecimal("-5000"));

        ApiException exception = assertThrows(ApiException.class, () -> LoanValidator.validateCreateLoanRequest(request));
        assertEquals("Principal amount must be greater than 0", exception.getMessage());
        
    }

    @Test
    void testCheckLoanLimitExceedValid() {
        Customer customer = new Customer();
        customer.setCreditLimit(new BigDecimal("10000"));

        assertDoesNotThrow(() -> LoanValidator.checkLoanLimitExceed(new BigDecimal("5000"), customer));
        assertDoesNotThrow(() -> LoanValidator.checkLoanLimitExceed(new BigDecimal("10000"), customer));
    }

    @Test
    void testCheckLoanLimitExceedInvalid() {
        Customer customer = new Customer();
        customer.setCreditLimit(new BigDecimal("10000"));

        ApiException exception = assertThrows(ApiException.class, () -> LoanValidator.checkLoanLimitExceed(new BigDecimal("15000"), customer));
        assertEquals("Customer exceeds credit limit!", exception.getMessage());
        
    }
}
