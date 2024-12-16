package com.ekiziltan.loan.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "installment_payment_lock")
public class InstallmentPaymentLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long loanId;

    private Long customerId;

    @Enumerated(EnumType.STRING)
    private LockStatus status;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
