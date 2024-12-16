package com.ekiziltan.loan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = LoanInstallment.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallment {

    public static final String TABLE_NAME = "loan_installments";
    public static final String COL_ID = "id";
    public static final String COL_LOAN = "loan_id";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_PAID_AMOUNT = "paid_amount";
    public static final String COL_DUE_DATE = "due_date";
    public static final String COL_PAYMENT_DATE = "payment_date";
    public static final String COL_IS_PAID = "is_paid";


    public static final String COL_PRINCIPAL_PORTION = "principal_portion";
    public static final String COL_INTEREST_PORTION = "interest_portion";
    public static final String COL_INSTALLMENT_INTEREST_RATE = "installment_interest_rate";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COL_ID)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = COL_LOAN, nullable = false)
    private Loan loan;

    @Column(name = COL_AMOUNT, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = COL_PAID_AMOUNT, precision = 19, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = COL_DUE_DATE)
    private LocalDate dueDate;

    @Column(name = COL_PAYMENT_DATE)
    private LocalDate paymentDate;

    @Column(name = COL_IS_PAID)
    private Boolean isPaid;

    @Column(name = COL_PRINCIPAL_PORTION, precision = 19, scale = 2)
    private BigDecimal principalPortion;

    @Column(name = COL_INTEREST_PORTION, precision = 19, scale = 2)
    private BigDecimal interestPortion;

    @Column(name = COL_INSTALLMENT_INTEREST_RATE, precision = 5, scale = 4)
    private BigDecimal installmentInterestRate;
}
