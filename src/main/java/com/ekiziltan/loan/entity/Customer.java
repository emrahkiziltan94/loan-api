package com.ekiziltan.loan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {


    public static final String TABLE_NAME = "customers";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_SURNAME = "surname";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_ROLE = "role"; // temporary it can be in role table.
    public static final String COL_CREDIT_LIMIT = "creditLimit";
    public static final String COL_USED_CREDIT_LIMIT = "usedCreditLimit";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COL_ID)
    private Long id;

    @Column(name = COL_NAME,nullable = false)
    private String name;

    @Column(name = COL_SURNAME,nullable = false)
    private String surname;

    @Column(name = COL_CREDIT_LIMIT, precision = 19, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = COL_USED_CREDIT_LIMIT, precision = 19, scale = 2)
    private BigDecimal usedCreditLimit;

    @Column(name = COL_USERNAME)
    private String username;

    @Column(name = COL_PASSWORD,nullable = false)
    private String password;

    @Column(name = COL_ROLE)
    private String role;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<Loan> loans = new ArrayList<>();
}
