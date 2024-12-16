package com.ekiziltan.loan.repository.specifications;



import com.ekiziltan.loan.entity.Loan;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class LoanSpecifications {

    public static Specification<Loan> hasCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Loan> hasNumberOfInstallment(Integer numberOfInstallment) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("numberOfInstallment"), numberOfInstallment);
    }

    public static Specification<Loan> hasCreateDateBetween(LocalDate from, LocalDate to) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("createDate"), from, to);
    }

    public static Specification<Loan> hasCreateDateAfterOrEqual(LocalDate from) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), from);
    }

    public static Specification<Loan> hasCreateDateBeforeOrEqual(LocalDate to) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createDate"), to);
    }

    public static Specification<Loan> hasIsPaid(Boolean isPaid) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isPaid"), isPaid);
    }
}
