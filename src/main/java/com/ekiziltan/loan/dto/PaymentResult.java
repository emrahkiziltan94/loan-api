package com.ekiziltan.loan.dto;

import com.ekiziltan.loan.entity.LoanInstallment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class PaymentResult {
    private int paidCount;
    private BigDecimal totalSpent;
    private List<LoanInstallment> installments;
}
