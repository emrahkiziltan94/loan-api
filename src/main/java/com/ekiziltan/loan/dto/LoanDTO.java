package com.ekiziltan.loan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Data Transfer Object representing a Loan")
public class LoanDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "Unique identifier of the loan", example = "1")
    private Long id;

    @Schema(description = "Unique identifier of the customer", example = "1001")
    private Long customerId;

    @Schema(description = "Total amount of the loan", example = "10000.00")
    private BigDecimal loanAmount;

    @Schema(description = "Interest rate of the loan", example = "0.1")
    private Double interestRate;

    @Schema(description = "principal amount of the loan", example = "10000.00")
    private BigDecimal principalAmount;

    @Schema(description = "interest amount of the loan", example = "1000.00")
    private BigDecimal interestAmount;

    @Schema(description = "Number of installments", example = "12")
    private Integer numberOfInstallment;

    @Schema(description = "Date when the loan was created", example = "2023-12-01")
    private LocalDate createDate;

    @Schema(description = "Indicates whether the loan has been fully paid", example = "false")
    private Boolean isPaid;
}
