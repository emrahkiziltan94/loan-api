package com.ekiziltan.loan.utils;

import com.ekiziltan.loan.config.security.CustomerPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityHelper {

    public  Long getCustomerIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomerPrincipal userDetails = (CustomerPrincipal) authentication.getPrincipal();
        Long customerId = userDetails.getCustomerId();
        return customerId;
    }
}
