package com.ekiziltan.loan.utils.mapper;

import com.ekiziltan.loan.dto.LoanDTO;
import com.ekiziltan.loan.dto.LoanInstallmentDTO;
import com.ekiziltan.loan.entity.Customer;
import com.ekiziltan.loan.entity.Loan;
import com.ekiziltan.loan.entity.LoanInstallment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanMapperTest {

    @Test
    void testEntityToDTO() {
        Loan loan = getLoan();

        LoanDTO dto = LoanMapper.entityToDTO(loan);

        assertNotNull(dto);
        assertEquals(loan.getId(), dto.getId());
        assertEquals(loan.getCustomer().getId(), dto.getCustomerId());
        assertEquals(loan.getLoanAmount(), dto.getLoanAmount());
        assertEquals(loan.getPrincipalAmount(), dto.getPrincipalAmount());
        assertEquals(loan.getInterestAmount(), dto.getInterestAmount());
        assertEquals(loan.getInterestRate(), dto.getInterestRate());
        assertEquals(loan.getNumberOfInstallment(), dto.getNumberOfInstallment());
        assertEquals(loan.getCreateDate(), dto.getCreateDate());
        assertEquals(loan.getIsPaid(), dto.getIsPaid());
    }

    private static Loan getLoan() {
        Customer customer = new Customer();
        customer.setId(1L);

        Loan loan = new Loan();
        loan.setId(100L);
        loan.setCustomer(customer);
        loan.setLoanAmount(new BigDecimal("10000"));
        loan.setPrincipalAmount(new BigDecimal("8000"));
        loan.setInterestAmount(new BigDecimal("2000"));
        loan.setInterestRate(new Double("5.5"));
        loan.setNumberOfInstallment(12);
        loan.setCreateDate(LocalDate.of(2023, 1, 1));
        loan.setIsPaid(false);
        return loan;
    }

    @Test
    void testEntityToInstallmentDTO() {
        LoanInstallment installment = getLoanInstallment();

        LoanInstallmentDTO dto = LoanMapper.entityToInstallmentDTO(installment);

        assertNotNull(dto);
        assertEquals(installment.getId(), dto.getId());
        assertEquals(installment.getLoan().getId(), dto.getLoanId());
        assertEquals(installment.getAmount(), dto.getAmount());
        assertEquals(installment.getInstallmentInterestRate(), dto.getInstallmentInterestRate());
        assertEquals(installment.getInterestPortion(), dto.getInterestPortion());
        assertEquals(installment.getPrincipalPortion(), dto.getPrincipalPortion());
        assertEquals(installment.getPaidAmount(), dto.getPaidAmount());
        assertEquals(installment.getDueDate(), dto.getDueDate());
        assertEquals(installment.getPaymentDate(), dto.getPaymentDate());
        assertEquals(installment.getIsPaid(), dto.getIsPaid());
    }

    private static LoanInstallment getLoanInstallment() {
        Loan loan = new Loan();
        loan.setId(100L);

        LoanInstallment installment = new LoanInstallment();
        installment.setId(200L);
        installment.setLoan(loan);
        installment.setAmount(new BigDecimal("1000"));
        installment.setInstallmentInterestRate(new BigDecimal("0.5"));
        installment.setInterestPortion(new BigDecimal("50"));
        installment.setPrincipalPortion(new BigDecimal("950"));
        installment.setPaidAmount(new BigDecimal("1000"));
        installment.setDueDate(LocalDate.of(2023, 2, 1));
        installment.setPaymentDate(LocalDate.of(2023, 2, 5));
        installment.setIsPaid(true);
        return installment;
    }
}

