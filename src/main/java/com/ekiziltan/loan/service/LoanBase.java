package com.ekiziltan.loan.service;
// loan marker
public interface LoanBase<T, R> {
    R execute(T request);
}
