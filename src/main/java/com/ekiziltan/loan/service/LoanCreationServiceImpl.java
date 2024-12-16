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
import com.ekiziltan.loan.service.lock.LoanApplicationLockService;
import com.ekiziltan.loan.utils.constants.LoanServiceConstants;
import com.ekiziltan.loan.utils.mapper.LoanMapper;
import com.ekiziltan.loan.validations.LoanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;

@RequiredArgsConstructor
@Service
public class LoanCreationServiceImpl implements LoanCreationService {
    private final LoanApplicationLockService lockService;
    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository installmentRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CacheEvict(value = {"loansCache", "installmentsCache"}, allEntries = true)
    public LoanDTO execute(CreateLoanRequest request) {

        lockService.createLock(request.getCustomerId());

        try {

            LoanValidator.validateCreateLoanRequest(request);
            Customer customer = fetchCustomer(request.getCustomerId());
            BigDecimal newUsedCredit = checkAndCalculateNewUsedCredit(customer, request.getPrincipalAmount());
            BigDecimal totalLoanAmount = calculateTotalLoanAmount(request);
            Loan loan = createLoanEntity(customer, totalLoanAmount, request);
            Loan savedLoan = loanRepository.save(loan);
            updateCustomerCredit(customer, newUsedCredit);
            createLoanInstallments(savedLoan);
            lockService.markLockAsDone(request.getCustomerId());
            return LoanMapper.entityToDTO(savedLoan);
        } catch (Exception e) {

            lockService.markLockAsFailed(request.getCustomerId());
            throw e;
        }
    }

    private Customer fetchCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException(
                        LoanServiceConstants.ERROR_CUSTOMER_NOT_FOUND + customerId,
                        HttpStatus.NOT_FOUND));
    }


    private BigDecimal calculateTotalLoanAmount(CreateLoanRequest request) {
        return getTotalLoanAmount(request);
    }


    private BigDecimal checkAndCalculateNewUsedCredit(Customer customer, BigDecimal principalAmount) {
        BigDecimal newUsed = customer.getUsedCreditLimit().add(principalAmount);
        LoanValidator.checkLoanLimitExceed(newUsed, customer);
        return newUsed;
    }

    private Loan createLoanEntity(Customer customer, BigDecimal totalLoanAmount, CreateLoanRequest request) {
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(totalLoanAmount);
        loan.setNumberOfInstallment(request.getNumberOfInstallment());
        loan.setInterestAmount(totalLoanAmount.subtract(request.getPrincipalAmount()));
        loan.setInterestRate(request.getInterestRate());
        loan.setPrincipalAmount(request.getPrincipalAmount());
        loan.setCreateDate(LocalDate.now(ZoneId.of("UTC")));
        loan.setIsPaid(false);
        return loan;
    }

    private void updateCustomerCredit(Customer customer, BigDecimal newUsedCredit) {
        customer.setUsedCreditLimit(newUsedCredit);
        customerRepository.save(customer);
    }
    // resource: https://en.wikipedia.org/wiki/Amortization_calculator
    private void createLoanInstallments(Loan loan) {

        double monthlyInterestRate = loan.getInterestRate()/12;


        double P = loan.getPrincipalAmount().doubleValue();
        int n = loan.getNumberOfInstallment();
        double i = monthlyInterestRate; // 0.01 gibi

        double monthlyPaymentDouble;
        if (i == 0.0) {

            monthlyPaymentDouble = P / n;
        } else {
            monthlyPaymentDouble = P * (i * Math.pow(1 + i, n)) / (Math.pow(1 + i, n) - 1);
        }

        BigDecimal monthlyPayment = BigDecimal.valueOf(monthlyPaymentDouble).setScale(2, RoundingMode.HALF_UP);

        BigDecimal currentBalance = loan.getPrincipalAmount();

        LocalDate firstInstallmentDueDate = LocalDate.now(ZoneId.of("UTC"))
                .plusMonths(1)
                .withDayOfMonth(1);

        for (int installmentIndex = 0; installmentIndex < n; installmentIndex++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setIsPaid(false);
            installment.setPaidAmount(BigDecimal.ZERO);
            installment.setPaymentDate(null);

            installment.setDueDate(firstInstallmentDueDate.plusMonths(installmentIndex));

            BigDecimal interestForThisInstallment = currentBalance
                    .multiply(BigDecimal.valueOf(monthlyInterestRate))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal principalForThisInstallment = monthlyPayment.subtract(interestForThisInstallment);


            if (principalForThisInstallment.compareTo(currentBalance) > 0) {
                principalForThisInstallment = currentBalance;
            }

            installment.setAmount(monthlyPayment);

            installment.setPrincipalPortion(principalForThisInstallment);
            installment.setInterestPortion(interestForThisInstallment);

            installment.setInstallmentInterestRate(BigDecimal.valueOf(monthlyInterestRate).setScale(4, RoundingMode.HALF_UP));


            installmentRepository.save(installment);

            currentBalance = currentBalance.subtract(principalForThisInstallment);
            if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
                currentBalance = BigDecimal.ZERO;
            }
        }
    }

    private  BigDecimal getTotalLoanAmount(CreateLoanRequest request) {
        BigDecimal interestFactor = BigDecimal.valueOf(1).add(BigDecimal.valueOf(request.getInterestRate()));
        return request.getPrincipalAmount().multiply(interestFactor);
    }


}
