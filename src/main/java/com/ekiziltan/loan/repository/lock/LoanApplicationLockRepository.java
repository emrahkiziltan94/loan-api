package com.ekiziltan.loan.repository.lock;

import com.ekiziltan.loan.entity.LoanApplicationLock;
import com.ekiziltan.loan.entity.LockStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanApplicationLockRepository extends JpaRepository<LoanApplicationLock, Long> {

    Optional<LoanApplicationLock> findByCustomerIdAndStatus(Long customerId, LockStatus status);

}
