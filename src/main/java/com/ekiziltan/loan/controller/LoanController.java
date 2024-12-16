package com.ekiziltan.loan.controller;

import com.ekiziltan.loan.dto.*;
import com.ekiziltan.loan.service.InstallmentListService;
import com.ekiziltan.loan.service.pay.InstallmentPayService;
import com.ekiziltan.loan.service.LoanCreationService;
import com.ekiziltan.loan.service.LoansListForCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@RestController
public class LoanController implements LoanApi {

    private final LoanCreationService loanCreationService;
    private final LoansListForCustomerService listLoansForCustomerService;
    private final InstallmentListService installmentListService;
    private final InstallmentPayService installmentPayService;


    @Value("${app.pagination.default-page:0}")
    private int defaultPage;

    @Value("${app.pagination.default-size:10}")
    private int defaultSize;


    @Override
    public ResponseEntity<LoanDTO> createLoan(CreateLoanRequest request) {
        LoanDTO createdLoan = loanCreationService.execute(request);
        return new ResponseEntity<>(createdLoan, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<LoanDTO>> listLoans(Long customerId, Integer pageNumber, Integer pageSize,
                                                   Integer numberOfInstallment, LocalDate createDateFrom, LocalDate createDateTo,
                                                   Boolean isPaid) {

        LoanListForCustomerRequest loanListForCustomerRequest = getLoanListForCustomerRequest(customerId, pageNumber, pageSize, numberOfInstallment, createDateFrom, createDateTo, isPaid);
        List<LoanDTO> loanList = listLoansForCustomerService.execute(loanListForCustomerRequest);

        if (loanList.isEmpty()) {

            return new ResponseEntity<>(List.of(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(loanList, HttpStatus.OK);
    }

    private static LoanListForCustomerRequest getLoanListForCustomerRequest(Long customerId, Integer pageNumber, Integer pageSize,
                                                                            Integer numberOfInstallment, LocalDate createDateFrom, LocalDate createDateTo,
                                                                            Boolean isPaid) {

        int page = (pageNumber != null) ? pageNumber : 0;
        int size = (pageSize != null) ? pageSize : 36;

        return LoanListForCustomerRequest.builder()
                .customerId(customerId)
                .pageable(PageRequest.of(page, size))
                .numberOfInstallment(numberOfInstallment)
                .createDateFrom(createDateFrom)
                .createDateTo(createDateTo)
                .isPaid(isPaid)
                .build();
    }


    @Override
    public ResponseEntity<List<LoanInstallmentDTO>> listInstallments(Long loanId, Integer pageNumber, Integer pageSize) {
        int page = (pageNumber != null) ? pageNumber : defaultPage;
        int size = (pageSize != null) ? pageSize : defaultSize;

        List<LoanInstallmentDTO> installmentDTOS = installmentListService.execute(ListInstallmentRequest
                .builder()
                .loanId(loanId)
                .pageable(PageRequest.of(page, size))
                .build());

        if (installmentDTOS.isEmpty()) {
            return new ResponseEntity<>(List.of(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(installmentDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PayInstallmentResponse> payLoan(PayInstallmentRequest request) {
        PayInstallmentResponse response = installmentPayService.execute(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
