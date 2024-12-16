package com.ekiziltan.loan.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class PayInstallmentResponse {
    private Integer paidInstalments;
    private BigDecimal totalSpent;
    private Boolean loanIsFullyPaid;

}
