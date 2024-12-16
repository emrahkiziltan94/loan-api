package com.ekiziltan.loan.service.pay;


import com.ekiziltan.loan.entity.LoanInstallment;
import com.ekiziltan.loan.repository.LoanInstallmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InstallmentProcessor {

    private final LoanInstallmentRepository installmentRepository;

    public boolean isAlreadyPaid(LoanInstallment installment) {
        return Boolean.TRUE.equals(installment.getIsPaid());
    }

    public boolean isWithinPaymentWindow(LoanInstallment installment, LocalDate now) {
        LocalDate limitDate = now.plusMonths(3).withDayOfMonth(1);
        return !installment.getDueDate().isAfter(limitDate);
    }

    public boolean canPayInstallment(BigDecimal amountLeft, BigDecimal finalAmount) {
        return amountLeft.compareTo(finalAmount) >= 0;
    }

    public void payInstallment(LoanInstallment installment, BigDecimal finalAmount, LocalDate paymentDate) {
        installment.setPaidAmount(finalAmount);
        installment.setIsPaid(true);
        installment.setPaymentDate(paymentDate);
        installmentRepository.save(installment);
    }
}
