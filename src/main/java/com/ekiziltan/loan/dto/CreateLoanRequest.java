package com.ekiziltan.loan.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.math.BigDecimal;

@Data
@Schema(description = "Request to create a new loan")
public class CreateLoanRequest {

    @Schema(description = "ID of the customer", example = "1001", required = true)
    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;

    @Schema(description = "Principal amount of the loan", example = "10000.00", required = true)
    @NotNull(message = "Principal amount cannot be null")
    private BigDecimal principalAmount;

    @Schema(description = "Interest rate of the loan (e.g., 0.1 for 10%)", example = "0.1", required = true)
    @NotNull(message = "Interest rate cannot be null")
    private Double interestRate; // 0.1 - 0.5

    @Schema(description = "Number of installments (e.g., 6, 9, 12, 24)", example = "12", required = true)
    @NotNull(message = "Number of installments cannot be null")
    private Integer numberOfInstallment; // 6, 9, 12, 24
}
