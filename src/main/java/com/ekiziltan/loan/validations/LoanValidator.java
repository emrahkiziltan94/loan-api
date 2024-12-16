package com.ekiziltan.loan.validations;

import com.ekiziltan.loan.dto.CreateLoanRequest;
import com.ekiziltan.loan.entity.Customer;
import com.ekiziltan.loan.handlers.exceptions.ApiException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

public class LoanValidator {

    private static final String ERROR_CUSTOMER_EXCEEDS_LIMIT = "Customer exceeds credit limit!";

    public static void validateLoanInstallments(Integer installments) {
        if (!List.of(6, 9, 12, 24).contains(installments)) {
            throw new ApiException("Installment count must be one of 6, 9, 12, 24", HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateInterestRate(Double interestRate) {
        if (interestRate < 0.1 || interestRate > 0.5) {
            throw new ApiException("Interest rate must be between 0.1 and 0.5", HttpStatus.BAD_REQUEST);
        }
    }

    public static void validatePrincipalAmount(BigDecimal principalAmount) {
        if (principalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("Principal amount must be greater than 0", HttpStatus.BAD_REQUEST);
        }
    }
    public static void validateCreateLoanRequest(CreateLoanRequest request){
        validateLoanInstallments(request.getNumberOfInstallment());
        validateInterestRate(request.getInterestRate());
        validatePrincipalAmount(request.getPrincipalAmount());

    }
    public static void checkLoanLimitExceed(BigDecimal newUsed, Customer customer) {
        if (newUsed.compareTo(customer.getCreditLimit()) > 0) {
            throw new ApiException(ERROR_CUSTOMER_EXCEEDS_LIMIT, HttpStatus.BAD_REQUEST);
        }
    }


}
