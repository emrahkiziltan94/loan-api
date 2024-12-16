package com.ekiziltan.loan.service;

import com.ekiziltan.loan.dto.ListInstallmentRequest;
import com.ekiziltan.loan.dto.LoanInstallmentDTO;
import com.ekiziltan.loan.entity.LoanInstallment;
import com.ekiziltan.loan.repository.LoanInstallmentRepository;
import com.ekiziltan.loan.utils.mapper.LoanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InstallmentListServiceImpl implements InstallmentListService {

    private final LoanInstallmentRepository installmentRepository;


    @Override
    @PreAuthorize("hasRole('ADMIN') or @loanOwnerSecurityService.isLoanOwner(#request.loanId, principal.customerId)")
    @Cacheable(value = "installmentsCache", key = "#request.loanId")
    public List<LoanInstallmentDTO> execute(ListInstallmentRequest request) {
        Page<LoanInstallment> installmentsPage = installmentRepository.findByLoan_IdOrderByDueDateAsc(request.getLoanId(),request.getPageable());
        return installmentsPage.map(LoanMapper::entityToInstallmentDTO).getContent();
    }
}
