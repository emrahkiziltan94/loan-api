package com.ekiziltan.loan.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Data
@Builder
public class LoanListForCustomerRequest {
    private Long customerId;
    private Pageable pageable;
    private Integer numberOfInstallment;
    private LocalDate createDateFrom;
    private LocalDate createDateTo;
    private Boolean isPaid;
}
