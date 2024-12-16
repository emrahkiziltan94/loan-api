package com.ekiziltan.loan.service.lock;

import com.ekiziltan.loan.entity.InstallmentPaymentLock;
import com.ekiziltan.loan.entity.LockStatus;
import com.ekiziltan.loan.handlers.exceptions.ApiException;
import com.ekiziltan.loan.repository.lock.InstallmentPaymentLockRepository;
import com.ekiziltan.loan.utils.SecurityHelper;
import com.ekiziltan.loan.utils.constants.LoanServiceConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class InstallmentPaymentLockService {

    private final InstallmentPaymentLockRepository installmentPaymentLockRepository;
    private final SecurityHelper securityHelper;

    @Transactional
    public void createLock(Long loanId) {
        installmentPaymentLockRepository.findByLoanIdAndStatus(loanId, LockStatus.IN_PROGRESS)
                .ifPresent(lock -> {
                    throw new ApiException(LoanServiceConstants.ERROR_ACTIVE_INSTALLMENT_PAYMENT, HttpStatus.CONFLICT);
                });

        InstallmentPaymentLock newLock = new InstallmentPaymentLock();
        newLock.setLoanId(loanId);
        newLock.setStatus(LockStatus.IN_PROGRESS);
        newLock.setCustomerId(securityHelper.getCustomerIdFromSecurityContext());
        newLock.setCreatedDate(LocalDateTime.now());
        installmentPaymentLockRepository.save(newLock);
    }

    public void checkLockExists(Long customerId) {
        installmentPaymentLockRepository.findByCustomerIdAndStatus(customerId, LockStatus.IN_PROGRESS)
                .ifPresent(lock -> {
                    throw new ApiException(LoanServiceConstants.ERROR_ACTIVE_LOCK_EXISTS, HttpStatus.CONFLICT);
                });
    }

    @Transactional
    public void markLockAsDone(Long loanId) {
        InstallmentPaymentLock lock = installmentPaymentLockRepository.findByLoanIdAndStatus(loanId, LockStatus.IN_PROGRESS)
                .orElseThrow(() -> new ApiException(LoanServiceConstants.ERROR_ACTIVE_LOCK_NOT_FOUND, HttpStatus.NOT_FOUND));

        lock.setStatus(LockStatus.DONE);
        lock.setUpdatedDate(LocalDateTime.now());
        installmentPaymentLockRepository.save(lock);
    }

    @Transactional
    public void markLockAsFailed(Long loanId) {
        installmentPaymentLockRepository.findByLoanIdAndStatus(loanId, LockStatus.IN_PROGRESS)
                .ifPresent(lock -> {
                    lock.setStatus(LockStatus.FAILED);
                    lock.setUpdatedDate(LocalDateTime.now());
                    installmentPaymentLockRepository.save(lock);
                });
    }
}
