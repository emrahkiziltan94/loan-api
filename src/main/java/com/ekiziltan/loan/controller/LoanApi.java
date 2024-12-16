package com.ekiziltan.loan.controller;

import com.ekiziltan.loan.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1")
public interface LoanApi {

    @Operation(summary = "Admin -> Create a new loan")
    @PostMapping("/admin/create-loan")
    ResponseEntity<LoanDTO> createLoan(@RequestBody CreateLoanRequest request);

    @Operation(summary = "Customer/Admin -> List loans for a customer (with pagination)")
    @GetMapping("/customer/list-loans/{customerId}")
    ResponseEntity<List<LoanDTO>> listLoans(
            @PathVariable Long customerId,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer numberOfInstallment,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createDateTo,
            @RequestParam(required = false) Boolean isPaid
    );

    @Operation(summary = "Customer/Admin -> List installments for a loan (with pagination)")
    @GetMapping("/customer/list-installments/{loanId}")
    ResponseEntity<List<LoanInstallmentDTO>> listInstallments(
            @PathVariable Long loanId,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize
    );

    @Operation(summary = "Customer/Admin -> Make a loan payment")
    @PostMapping("/customer/pay-loan")
    ResponseEntity<PayInstallmentResponse> payLoan(@RequestBody PayInstallmentRequest request);
}
