package com.ekiziltan.loan.repository.lock;

import com.ekiziltan.loan.entity.InstallmentPaymentLock;
import com.ekiziltan.loan.entity.LockStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstallmentPaymentLockRepository extends JpaRepository<InstallmentPaymentLock, Long> {

    Optional<InstallmentPaymentLock> findByLoanIdAndStatus(Long loanId, LockStatus status);
}
