package com.ekiziltan.loan.service;

import com.ekiziltan.loan.dto.LoanDTO;
import com.ekiziltan.loan.dto.LoanListForCustomerRequest;
import com.ekiziltan.loan.entity.Loan;
import com.ekiziltan.loan.repository.LoanRepository;
import com.ekiziltan.loan.repository.specifications.LoanSpecifications;
import com.ekiziltan.loan.utils.mapper.LoanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LoansListForCustomerServiceImpl implements LoansListForCustomerService {

    private final LoanRepository loanRepository;


    @Override
    @PreAuthorize("hasRole('ADMIN') or #request.customerId == principal.customerId")
    @Cacheable(value = "loansCache", key = "{#request.customerId, #request.pageable.pageNumber, #request.pageable.pageSize, " +
            "#request.numberOfInstallment, #request.createDateFrom, #request.createDateTo, " +
            "#request.isPaid}")
    public List<LoanDTO> execute(LoanListForCustomerRequest request) {
        Specification<Loan> spec = Specification.where(LoanSpecifications.hasCustomerId(request.getCustomerId()));

        spec = getLoanSpecificationOptionalCriterias(request.getNumberOfInstallment(), request.getCreateDateFrom(),
                request.getCreateDateTo(), request.getIsPaid(),
                spec);

        Page<Loan> loanPage = loanRepository.findAll(spec, request.getPageable());
        return new ArrayList<>(loanPage.map(LoanMapper::entityToDTO).getContent());
    }

    private static Specification<Loan> getLoanSpecificationOptionalCriterias(Integer numberOfInstallment, LocalDate createDateFrom, LocalDate createDateTo, Boolean isPaid, Specification<Loan> spec) {

        if (numberOfInstallment != null) {
            spec = spec.and(LoanSpecifications.hasNumberOfInstallment(numberOfInstallment));
        }
        if (createDateFrom != null && createDateTo != null) {
            spec = spec.and(LoanSpecifications.hasCreateDateBetween(createDateFrom, createDateTo));
        } else if (createDateFrom != null) {
            spec = spec.and(LoanSpecifications.hasCreateDateAfterOrEqual(createDateFrom));
        } else if (createDateTo != null) {
            spec = spec.and(LoanSpecifications.hasCreateDateBeforeOrEqual(createDateTo));
        }

        if (isPaid != null) {
            spec = spec.and(LoanSpecifications.hasIsPaid(isPaid));
        }
        return spec;
    }


}
