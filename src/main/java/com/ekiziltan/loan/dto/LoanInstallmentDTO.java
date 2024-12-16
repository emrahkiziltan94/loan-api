package com.ekiziltan.loan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object representing a Loan Installment.
 */
@Data
@Schema(description = "Data Transfer Object representing a Loan Installment")
public class LoanInstallmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(
            description = "Unique identifier of the loan installment",
            example = "1",
            required = true
    )
    private Long id;

    @Schema(
            description = "Unique identifier of the associated loan",
            example = "1001",
            required = true
    )
    private Long loanId;

    @Schema(
            description = "Total amount of the installment (principal + interest)",
            example = "5000.00",
            required = true
    )
    private BigDecimal amount;

    @Schema(
            description = "Amount that has been paid towards this installment",
            example = "0.00",
            required = true
    )
    private BigDecimal paidAmount;

    @Schema(
            description = "Due date of the installment",
            example = "2024-05-01",
            required = true,
            type = "string",
            format = "date"
    )
    private LocalDate dueDate;

    @Schema(
            description = "Payment date of the installment (if paid)",
            example = "2024-05-02",
            required = false,
            type = "string",
            format = "date"
    )
    private LocalDate paymentDate;

    @Schema(
            description = "Indicates whether the installment has been paid",
            example = "false",
            required = true
    )
    private Boolean isPaid;

    @Schema(
            description = "Portion of the installment amount that goes towards the principal",
            example = "4000.00",
            required = true
    )
    private BigDecimal principalPortion;

    @Schema(
            description = "Portion of the installment amount that goes towards the interest",
            example = "1000.00",
            required = true
    )
    private BigDecimal interestPortion;

    @Schema(
            description = "Interest rate applied to this installment (monthly rate as a decimal, e.g., 0.01 for 1%)",
            example = "0.0125",
            required = true
    )
    private BigDecimal installmentInterestRate;
}
