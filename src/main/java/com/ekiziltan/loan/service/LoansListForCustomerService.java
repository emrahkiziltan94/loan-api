package com.ekiziltan.loan.service;

import com.ekiziltan.loan.dto.LoanDTO;
import com.ekiziltan.loan.dto.LoanListForCustomerRequest;

import java.util.List;

public interface LoansListForCustomerService extends LoanBase<LoanListForCustomerRequest, List<LoanDTO>> {
}
