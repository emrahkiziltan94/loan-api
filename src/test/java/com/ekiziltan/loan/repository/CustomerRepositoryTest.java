package com.ekiziltan.loan.repository;

import com.ekiziltan.loan.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @Order(1)
    @DisplayName("Should return empty when username does not exist")
    public void testFindByUsernameNotExists() {
        // Act
        Optional<Customer> found = customerRepository.findByUsername("nonexistent_user");

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("Should return customer when username exists")
    public void testFindByUsernameExists() {
        // Arrange
        Customer customer = new Customer();
        customer.setUsername("john_doe");
        customer.setPassword("password");
        customer.setName("John");
        customer.setSurname("Doe");
        customerRepository.save(customer);

        // Act
        Optional<Customer> found = customerRepository.findByUsername("john_doe");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john_doe");
    }


}
