package com.ekiziltan.loan.config.security;

import com.ekiziltan.loan.entity.Customer;
import com.ekiziltan.loan.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {


    private final CustomerRepository customerRepository;


    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (customerRepository.count() == 0) {

            Customer admin = new Customer();
            admin.setName("Ali");
            admin.setSurname("Veli");
            admin.setCreditLimit(BigDecimal.valueOf(100000));
            admin.setUsedCreditLimit(BigDecimal.ZERO);
            admin.setUsername("admin");                // login username
            admin.setPassword(passwordEncoder.encode("admin123")); // BCrypt encode
            admin.setRole("ROLE_ADMIN");
            customerRepository.save(admin);

            Customer c1 = new Customer();
            c1.setName("Ayse");
            c1.setSurname("Fatma");
            c1.setCreditLimit(BigDecimal.valueOf(50000));
            c1.setUsedCreditLimit(BigDecimal.ZERO);
            c1.setUsername("customer1");
            c1.setPassword(passwordEncoder.encode("cust123"));
            c1.setRole("ROLE_CUSTOMER");
            customerRepository.save(c1);

            Customer c2 = new Customer();
            c2.setName("Mehmet");
            c2.setSurname("Can");
            c2.setCreditLimit(BigDecimal.valueOf(75000));
            c2.setUsedCreditLimit(BigDecimal.ZERO);
            c2.setUsername("customer2");
            c2.setPassword(passwordEncoder.encode("cust456"));
            c2.setRole("ROLE_CUSTOMER");
            customerRepository.save(c2);

            log.info(">>> 1 Admin + 2 Customer is created!");
        } else {
            log.error(">>> DB is already initialized!");
        }
    }
}
