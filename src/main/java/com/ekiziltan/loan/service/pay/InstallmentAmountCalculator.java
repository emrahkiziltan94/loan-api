package com.ekiziltan.loan.service.pay;


import com.ekiziltan.loan.entity.LoanInstallment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class InstallmentAmountCalculator {

    public BigDecimal calculateFinalAmount(LoanInstallment inst, LocalDate now) {
        if (now.isBefore(inst.getDueDate())) {
            return applyDiscount(inst, now);
        } else if (now.isAfter(inst.getDueDate())) {
            return applyPenalty(inst, now);
        }
        return inst.getAmount();
    }

    private BigDecimal applyDiscount(LoanInstallment inst, LocalDate now) {
        long daysEarly = ChronoUnit.DAYS.between(now, inst.getDueDate());
        BigDecimal discount = inst.getAmount()
                .multiply(BigDecimal.valueOf(0.001))
                .multiply(BigDecimal.valueOf(daysEarly));
        return inst.getAmount().subtract(discount);
    }

    private BigDecimal applyPenalty(LoanInstallment inst, LocalDate now) {
        long daysLate = ChronoUnit.DAYS.between(inst.getDueDate(), now);
        BigDecimal penalty = inst.getAmount()
                .multiply(BigDecimal.valueOf(0.001))
                .multiply(BigDecimal.valueOf(daysLate));
        return inst.getAmount().add(penalty);
    }
}
