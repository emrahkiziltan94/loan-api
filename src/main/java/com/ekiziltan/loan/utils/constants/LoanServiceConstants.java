package com.ekiziltan.loan.utils.constants;



public final class LoanServiceConstants {

    public static final String ERROR_ACTIVE_LOCK_EXISTS = "Active loan application lock exists for this customer.";

    private LoanServiceConstants() {
        throw new UnsupportedOperationException("Cannot instantiate LoanServiceConstants.");
    }


    public static final String ERROR_CUSTOMER_NOT_FOUND = "Customer is not found with given customerId: ";
    public static final String ERROR_ACTIVE_LOAN_APPLICATION = "There is an active loan application for this customer.";
    public static final String ERROR_ACTIVE_LOCK_NOT_FOUND = "Active loan application lock not found.";
    public static final String ERROR_ACTIVE_INSTALLMENT_PAYMENT = "There is an active installment payment for this loan.";
}

