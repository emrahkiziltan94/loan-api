package com.ekiziltan.loan.controller;

import com.ekiziltan.loan.dto.*;
import com.ekiziltan.loan.service.InstallmentListService;
import com.ekiziltan.loan.service.LoanCreationService;
import com.ekiziltan.loan.service.LoansListForCustomerService;
import com.ekiziltan.loan.service.pay.InstallmentPayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoanControllerStandaloneTest {

    @Mock
    private LoanCreationService loanCreationService;
    @Mock
    private LoansListForCustomerService loansListForCustomerService;
    @Mock
    private InstallmentListService installmentListService;
    @Mock
    private InstallmentPayService installmentPayService;
    @InjectMocks
    private LoanController loanController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();
    }

    @Test
    void createLoanSuccess() throws Exception {
        LoanDTO mockLoan = new LoanDTO();
        mockLoan.setId(1L);
        mockLoan.setCustomerId(10L);
        mockLoan.setLoanAmount(new BigDecimal(1200));
        mockLoan.setPrincipalAmount(new BigDecimal("1000"));

        when(loanCreationService.execute(any(CreateLoanRequest.class))).thenReturn(mockLoan);

        mockMvc.perform(post("/api/v1/admin/create-loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 10,
                                  "principalAmount": 1000,
                                  "numberOfInstallment": 6,
                                  "interestRate": 0.2
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerId", is(10)))
                .andExpect(jsonPath("$.loanAmount", is(1200)));

    }

    @Test
    void listLoansNoContent() throws Exception {
        when(loansListForCustomerService.execute(any(LoanListForCustomerRequest.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/customer/list-loans/{customerId}", 10L))
                .andExpect(status().isNoContent());
    }

    @Test
    void listLoansWithContent() throws Exception {
        LoanDTO dto = new LoanDTO();
        dto.setId(2L);
        dto.setCustomerId(10L);
        dto.setLoanAmount(new BigDecimal("2000"));
        dto.setPrincipalAmount(new BigDecimal("1500"));

        when(loansListForCustomerService.execute(any(LoanListForCustomerRequest.class)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/customer/list-loans/{customerId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].customerId", is(10)))
                .andExpect(jsonPath("$[0].loanAmount", is(2000)));
    }

    @Test
    void listInstallmentsNoContent() throws Exception {
        when(installmentListService.execute(any(ListInstallmentRequest.class)))
                .thenReturn(Collections.emptyList());


        mockMvc.perform(get("/api/v1/customer/list-installments/{loanId}", 99L)
                        .param("pageSize", "36"))
                .andExpect(status().isNoContent());
    }

    @Test
    void listInstallmentsWithContent() throws Exception {
        LoanInstallmentDTO installmentDTO = new LoanInstallmentDTO();
        installmentDTO.setId(100L);
        installmentDTO.setLoanId(99L);
        installmentDTO.setAmount(new BigDecimal("500"));

        when(installmentListService.execute(any(ListInstallmentRequest.class)))
                .thenReturn(List.of(installmentDTO));

        mockMvc.perform(get("/api/v1/customer/list-installments/{loanId}", 99L)
                        .param("pageSize", "36"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(100)))
                .andExpect(jsonPath("$[0].loanId", is(99)))
                .andExpect(jsonPath("$[0].amount", is(500)));
    }

    @Test
    void payLoanSuccess() throws Exception {
        PayInstallmentResponse response = PayInstallmentResponse.builder().loanIsFullyPaid(Boolean.FALSE).totalSpent(BigDecimal.valueOf(1000)).paidInstalments(1).build();


        when(installmentPayService.execute(any(PayInstallmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/customer/pay-loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loanId": 123,
                                  "installmentId": 456,
                                  "paymentAmount": 500
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanIsFullyPaid", is(false)))
                .andExpect(jsonPath("$.totalSpent", is(1000)))
                .andExpect(jsonPath("$.paidInstalments", is(1)));
    }
}
