package com.ekiziltan.loan.controller;

import com.ekiziltan.loan.config.security.JwtTokenProvider;
import com.ekiziltan.loan.dto.LoginRequest;
import com.ekiziltan.loan.dto.LoginResponse;
import com.ekiziltan.loan.entity.Customer;
import com.ekiziltan.loan.handlers.exceptions.ApiException;
import com.ekiziltan.loan.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomerRepository customerRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        Customer customer = customerRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("User not found!", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new ApiException("Password is wrong",HttpStatus.BAD_REQUEST);
        }

        String token = jwtTokenProvider.generateToken(
                customer.getId(),
                customer.getUsername(),
                customer.getRole()
        );

        return ResponseEntity.ok(new LoginResponse(token, customer.getId(), customer.getRole()));
    }

}
