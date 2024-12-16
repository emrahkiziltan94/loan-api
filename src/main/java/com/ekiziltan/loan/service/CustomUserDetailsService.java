package com.ekiziltan.loan.service;



import com.ekiziltan.loan.config.security.CustomerPrincipal;
import com.ekiziltan.loan.entity.Customer;
import com.ekiziltan.loan.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new CustomerPrincipal(
                customer.getId(),
                customer.getUsername(),
                customer.getPassword(),
                customer.getRole()
        );
    }
}
