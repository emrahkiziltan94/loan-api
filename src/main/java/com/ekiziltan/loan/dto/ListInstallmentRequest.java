package com.ekiziltan.loan.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

@Builder
@Data
public class ListInstallmentRequest {
    private Long loanId;
    private Pageable pageable;

}
