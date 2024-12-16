package com.ekiziltan.loan.service;

import com.ekiziltan.loan.dto.ListInstallmentRequest;
import com.ekiziltan.loan.dto.LoanInstallmentDTO;

import java.util.List;

public interface InstallmentListService extends LoanBase<ListInstallmentRequest,List<LoanInstallmentDTO>> {
}
