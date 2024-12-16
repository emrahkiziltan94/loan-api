package com.ekiziltan.loan.service;

import com.ekiziltan.loan.dto.CreateLoanRequest;
import com.ekiziltan.loan.dto.LoanDTO;
import com.ekiziltan.loan.entity.Customer;
import com.ekiziltan.loan.entity.Loan;
import com.ekiziltan.loan.entity.LoanInstallment;
import com.ekiziltan.loan.handlers.exceptions.ApiException;
import com.ekiziltan.loan.repository.CustomerRepository;
import com.ekiziltan.loan.repository.LoanInstallmentRepository;
import com.ekiziltan.loan.repository.LoanRepository;
import com.ekiziltan.loan.service.lock.InstallmentPaymentLockService;
import com.ekiziltan.loan.service.lock.LoanApplicationLockService;
import com.ekiziltan.loan.utils.SecurityHelper;
import com.ekiziltan.loan.utils.constants.LoanServiceConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanCreationServiceImplTest {

    @Mock
    private LoanApplicationLockService lockService;

    @Mock
    private InstallmentPaymentLockService installmentPaymentLockService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanInstallmentRepository installmentRepository;

    @Mock
    private SecurityHelper securityHelper;
    @InjectMocks
    private LoanCreationServiceImpl loanCreationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteSuccess() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setCustomerId(1L);
        request.setPrincipalAmount(new BigDecimal("1000"));
        request.setNumberOfInstallment(12);
        request.setInterestRate(0.2);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUsedCreditLimit(BigDecimal.ZERO);
        customer.setCreditLimit(new BigDecimal("10000"));

        doNothing().when(installmentPaymentLockService).checkLockExists(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            loan.setId(123L);
            return loan;
        });
        when(securityHelper.getCustomerIdFromSecurityContext()).thenReturn(1L);

        LoanDTO result = loanCreationService.execute(request);

        verify(lockService).createLock(1L);
        verify(lockService).markLockAsDone(1L);
        verify(customerRepository).save(customer);
        verify(loanRepository).save(any(Loan.class));
        verify(installmentRepository, times(request.getNumberOfInstallment())).save(any(LoanInstallment.class));
        assertNotNull(result);
        assertEquals(123L, result.getId());
    }

    @Test
    void testExecuteCustomerNotFound() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setCustomerId(2L);
        request.setPrincipalAmount(new BigDecimal("1000"));
        request.setNumberOfInstallment(12);
        request.setInterestRate(0.2);

        doNothing().when(installmentPaymentLockService).checkLockExists(1L);
        when(securityHelper.getCustomerIdFromSecurityContext()).thenReturn(1L);
        when(customerRepository.findById(2L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> loanCreationService.execute(request));
        verify(lockService).createLock(2L);
        verify(lockService).markLockAsFailed(2L);

        assertTrue(ex.getMessage().contains(LoanServiceConstants.ERROR_CUSTOMER_NOT_FOUND + "2"));
    }

    @Test
    void testExecuteLoanLimitExceeded() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setCustomerId(3L);
        request.setPrincipalAmount(new BigDecimal("10000"));
        request.setNumberOfInstallment(12);
        request.setInterestRate(0.2);

        Customer customer = new Customer();
        customer.setId(3L);
        customer.setUsedCreditLimit(new BigDecimal("5000"));
        customer.setCreditLimit(new BigDecimal("10000"));
        doNothing().when(installmentPaymentLockService).checkLockExists(1L);
        when(securityHelper.getCustomerIdFromSecurityContext()).thenReturn(1L);

        when(customerRepository.findById(3L)).thenReturn(Optional.of(customer));

        ApiException ex = assertThrows(ApiException.class, () -> loanCreationService.execute(request));
        verify(lockService).createLock(3L);
        verify(lockService).markLockAsFailed(3L);

        assertEquals("Customer exceeds credit limit!", ex.getMessage());
    }

    @Test
    void testExecuteValidationError() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setCustomerId(4L);
        request.setPrincipalAmount(BigDecimal.ZERO);
        request.setNumberOfInstallment(12);
        request.setInterestRate(0.2);

        doNothing().when(installmentPaymentLockService).checkLockExists(1L);
        when(securityHelper.getCustomerIdFromSecurityContext()).thenReturn(1L);
        Customer customer = new Customer();
        customer.setId(4L);
        customer.setUsedCreditLimit(BigDecimal.ZERO);
        customer.setCreditLimit(new BigDecimal("5000"));

        when(customerRepository.findById(4L)).thenReturn(Optional.of(customer));

        doThrow(new ApiException("Principal amount must be greater than 0", HttpStatus.BAD_REQUEST))
                .when(lockService).markLockAsFailed(4L);

        assertThrows(ApiException.class, () -> loanCreationService.execute(request));
        verify(lockService).createLock(4L);
        verify(lockService).markLockAsFailed(4L);
    }

    @Test
    void testExecuteLockServiceThrowsException() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setCustomerId(5L);
        request.setPrincipalAmount(new BigDecimal("1000"));
        request.setNumberOfInstallment(12);
        request.setInterestRate(0.2);

        doThrow(new RuntimeException("Lock creation failed")).when(lockService).createLock(5L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanCreationService.execute(request));
        assertEquals("Lock creation failed", ex.getMessage());
    }




}
