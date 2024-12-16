package com.ekiziltan.loan.service.pay;

import com.ekiziltan.loan.dto.PayInstallmentRequest;
import com.ekiziltan.loan.dto.PayInstallmentResponse;
import com.ekiziltan.loan.dto.PaymentResult;
import com.ekiziltan.loan.entity.Loan;
import com.ekiziltan.loan.entity.LoanInstallment;
import com.ekiziltan.loan.handlers.exceptions.ApiException;
import com.ekiziltan.loan.repository.LoanInstallmentRepository;
import com.ekiziltan.loan.repository.LoanRepository;
import com.ekiziltan.loan.service.lock.InstallmentPaymentLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@Service
public class InstallmentPayServiceImpl implements InstallmentPayService {

    private static final String ERROR_LOAN_NOT_FOUND = "Loan not found.";

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository installmentRepository;
    private final InstallmentPaymentLockService lockService;
    private final InstallmentAmountCalculator amountCalculator;
    private final InstallmentProcessor installmentProcessor;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @loanOwnerSecurityService.isLoanOwner(#request.loanId, principal.customerId)")
    @CacheEvict(value = {"loansCache", "installmentsCache"}, allEntries = true)
    public PayInstallmentResponse execute(PayInstallmentRequest request) {
        lockService.createLock(request.getLoanId());

        try {
            Loan loan = fetchLoanWithLock(request.getLoanId());
            List<LoanInstallment> installments = fetchPendingInstallments(loan.getId());

            PaymentResult paymentResult = processPayments(installments, request.getPayAmount());

            updateLoanStatusIfNecessary(loan, installments);

            lockService.markLockAsDone(request.getLoanId());

            return buildResponse(paymentResult, installments);
        } catch (ApiException e) {
            lockService.markLockAsFailed(request.getLoanId());
            throw e;
        }
    }

    private Loan fetchLoanWithLock(Long loanId) {
        return loanRepository.findByIdWithLock(loanId)
                .orElseThrow(() -> new ApiException(ERROR_LOAN_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private List<LoanInstallment> fetchPendingInstallments(Long loanId) {
        return installmentRepository.findByLoan_IdOrderByDueDateAsc(loanId);
    }

    private PaymentResult processPayments(List<LoanInstallment> installments, BigDecimal payAmount) {
        BigDecimal amountLeft = payAmount;
        int paidCount = 0;
        BigDecimal totalSpent = BigDecimal.ZERO;
        LocalDate now = LocalDate.now(ZoneId.of("UTC"));

        for (LoanInstallment installment : installments) {
            if (installmentProcessor.isAlreadyPaid(installment)) {
                continue;
            }

            if (!installmentProcessor.isWithinPaymentWindow(installment, now)) {
                break;
            }

            BigDecimal finalAmount = amountCalculator.calculateFinalAmount(installment, now);

            if (installmentProcessor.canPayInstallment(amountLeft, finalAmount)) {
                installmentProcessor.payInstallment(installment, finalAmount, now);
                paidCount++;
                totalSpent = totalSpent.add(finalAmount);
                amountLeft = amountLeft.subtract(finalAmount);
            } else {
                break;
            }
        }

        return new PaymentResult(paidCount, totalSpent, installments);
    }

    private void updateLoanStatusIfNecessary(Loan loan, List<LoanInstallment> installments) {
        boolean allPaid = installments.stream().allMatch(LoanInstallment::getIsPaid);
        if (allPaid && !loan.getIsPaid()) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
        }
    }

    private PayInstallmentResponse buildResponse(PaymentResult result, List<LoanInstallment> installments) {
        boolean allPaid = installments.stream().allMatch(LoanInstallment::getIsPaid);
        return PayInstallmentResponse.builder()
                .paidInstalments(result.getPaidCount())
                .totalSpent(result.getTotalSpent())
                .loanIsFullyPaid(allPaid)
                .build();
    }
}
