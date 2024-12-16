package com.ekiziltan.loan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    public static final String TABLE_NAME = "loans";
    public static final String COL_ID = "id";
    public static final String COL_CUSTOMER = "customer_id";
    public static final String COL_LOAN_AMOUNT = "loan_amount";
    public static final String COL_PRINCIPAL_AMOUNT = "principal_amount";
    public static final String COL_INTEREST_AMOUNT = "interest_amount";
    public static final String COL_NUM_OF_INSTALLMENT = "number_of_installment";
    public static final String COL_INTEREST_RATE = "interest_rate";
    public static final String COL_CREATE_DATE = "create_date";
    public static final String COL_IS_PAID = "is_paid";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COL_ID)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = COL_CUSTOMER, nullable = false)
    private Customer customer;

    @Column(name = COL_LOAN_AMOUNT, precision = 19, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = COL_NUM_OF_INSTALLMENT)
    private Integer numberOfInstallment;

    @Column(name = COL_INTEREST_RATE)
    private Double interestRate;

    @Column(name = COL_PRINCIPAL_AMOUNT, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = COL_INTEREST_AMOUNT, precision = 19, scale = 2)
    private BigDecimal interestAmount;

    @Column(name = COL_CREATE_DATE)
    private LocalDate createDate;

    @Column(name = COL_IS_PAID)
    private Boolean isPaid;


    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<LoanInstallment> installments = new ArrayList<>();
}
