package com.ekiziltan.loan.service;

public interface LoanOwnerSecurityService {
    boolean isLoanOwner(Long loanId, Long customerId);
}
