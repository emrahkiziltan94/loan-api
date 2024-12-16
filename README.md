This guide covers an overview, technologies used, setup steps, configuration details, deployment process, and additional important aspects such as testing, security, and database structure. Follow this guide to clone, build, and run the application seamlessly.

---

# **Loan API Project**

**Project Goal:**  
A simple loan management application providing features such as loan creation, listing loans (using various filters) for a customer, viewing loan installments, and paying installments.

## **Table of Contents**
1. [Project Overview](#project-overview)  
2. [Technologies Used](#technologies-used)  
3. [Folder Structure](#folder-structure)  
4. [Prerequisites](#prerequisites)  
5. [Setup and Run](#setup-and-run)  
   - [1. Cloning the Repository](#1-cloning-the-repository)  
   - [2. Building with Maven](#2-building-with-maven)  
   - [3. Running the Application](#3-running-the-application)  
6. [Profile Management](#profile-management)  
7. [API Endpoints and Swagger Documentation](#api-endpoints-and-swagger-documentation)  
8. [Docker and Deployment](#docker-and-deployment)  
   - [1. Dockerfile](#1-dockerfile)  
   - [2. Docker Compose (Optional)](#2-docker-compose-optional)  
   - [3. Running in Production and Test Environments](#3-running-in-production-and-test-environments)  
9. [Default Application Settings](#default-application-settings)  
10. [Testing with JaCoCo](#testing-with-jacoco)  
11. [Amortization Calculator](#amortization-calculator)  
12. [Lock Mechanisms and Concurrency Control](#lock-mechanisms-and-concurrency-control)  
13. [Connection Pool Configuration](#connection-pool-configuration)  
14. [Customer Credit Limit Management](#customer-credit-limit-management)  
15. [Database Initialization](#database-initialization)  
16. [Security and Access Control](#security-and-access-control)  
17. [Loan Listing with Specifications](#loan-listing-with-specifications)  
18. [Unit Testing Strategy](#unit-testing-strategy)  
19. [Contribution and License](#contribution-and-license)

---

## **Project Overview**

This project is a simple **Loan API** that allows basic **loan management** operations:

- **Create a Loan**  
- **List Loans for a Customer**  
- **List Installments**  
- **Pay Installments**

It uses **Spring Boot** to create a **RESTful API** with **JWT** authentication.

---

## **Technologies Used**

1. **Java 21**  
2. **Spring Boot 3.4.0**  
3. **Maven 3.8.7**  
4. **H2 Database** (for development and test profiles)  
5. **MySQL or PostgreSQL** (for production profile)  
6. **Hazelcast Cache**  
7. **OpenAPI/Swagger** for API documentation  
8. **Docker** for containerization  
9. **JaCoCo** for code coverage  
10. **Hibernate** for ORM  
11. **Spring Security** for authentication and authorization

---

## **Folder Structure**

```plaintext
loan-api
├── src
│   ├── main
│   │   ├── java/com/ekiziltan/loan
│   │   │   ├── config
│   │   │   │   ├── security
│   │   │   ├── controller
│   │   │   ├── dto
│   │   │   ├── entity
│   │   │   ├── exception
│   │   │   ├── repository
│   │   │   ├── service
│   │   │   │   ├── impl
│   │   │   └── LoanApiApplication.java
│   │   └── resources
│   │       ├── application.yml
│   │       ├── application-test.yml
│   │       ├── application-prod.yml
│   │       
│   └── test
│       └── java/com/ekiziltan/loan
├── Dockerfile
├── docker-compose.yml (optional)
├── .dockerignore
├── pom.xml
└── README.md
```

---

## **Prerequisites**

- **Java 21** installed  
- **Maven 3.8.7** or higher  
- **Docker** (optional, for containerization and deployment)

---

## **Setup and Run**

### **1. Cloning the Repository**

```bash
git clone https://github.com/username/loan-api.git
cd loan-api
```

### **2. Building with Maven**

```bash
mvn clean package
```
- This command will generate an executable **`loan-api-1.0.0.jar`** inside the `target/` folder.
- To skip tests, use: `mvn clean package -DskipTests`

### **3. Running the Application**


#### **Test Profile**

```bash
java -jar target/loan-api-1.0.0.jar --spring.profiles.active=test
```

#### **Production Profile**

```bash
java -jar target/loan-api-1.0.0.jar --spring.profiles.active=prod \
  --DB_USERNAME=your_db_user \
  --DB_PASSWORD=your_db_password
```

---

## **Profile Management**

- **`application.yml`**: Shared settings for all profiles.  
- **`application-test.yml`**: Test profile (create-drop, show-sql disabled, etc.)  
- **`application-prod.yml`**: Production profile (MySQL/PostgreSQL, show-sql disabled)

**Selecting a Profile:**

```bash
java -jar loan-api-1.0.0.jar --spring.profiles.active=prod
```

---

## **API Endpoints and Swagger Documentation**

This project uses **SpringDoc** to automatically generate **OpenAPI**/Swagger documentation.

- **Swagger UI Link:** `http://localhost:8080/loan/swagger-ui.html`  
- **OpenAPI JSON:** `http://localhost:8080/loan/api-docs`

### **Sample Endpoints:**

1. **Create Loan**
   - **Endpoint:** `POST /api/v1/admin/create-loan`  
   - **Request Body:** `CreateLoanRequest`  
   - **Response:** `LoanDTO`

2. **List Loans for Customer**
   - **Endpoint:** `GET /api/v1/customer/list-loans/{customerId}`  
   - **Response:** `List<LoanDTO>`

3. **List Installments**
   - **Endpoint:** `GET /api/v1/customer/list-installments/{loanId}`  
   - **Response:** `List<LoanInstallmentDTO>`

4. **Pay Loan (Installments)**
   - **Endpoint:** `POST /api/v1/customer/pay-loan`  
   - **Request Body:** `PayLoanRequest`  
   - **Response:** `PayLoanResponse`

---

## **Docker and Deployment**

We utilize a **multi-stage Dockerfile** to containerize the application, allowing for efficient builds and deployments.

### **1. Dockerfile**

```dockerfile
# Stage 1: Build
FROM maven:3.8.7-openjdk-21 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:21-jdk-slim
ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=UTC
WORKDIR /app
COPY --from=build /app/target/loan-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

### **2. Docker Compose (Optional)**

If you want to run the app along with a database container in a **production** environment, you can use a **`docker-compose.yml`**:

```yaml
version: '3.8'

services:
  loan-api:
    image: your-dockerhub-username/loan-api:latest
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_USERNAME: your_db_user
      DB_PASSWORD: your_db_password
    depends_on:
      - mysql-db

  mysql-db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: loan_db
      MYSQL_USER: your_db_user
      MYSQL_PASSWORD: your_db_password
      MYSQL_ROOT_PASSWORD: your_root_password
    ports:
      - "3306:3306"
```

### **3. Running in Production and Test Environments**

#### **Build the Docker Image:**

```bash
docker build -t loan-api:latest .
```

#### **Test Environment**

```bash
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=test \
  loan-api:latest
```

#### **Production Environment**

```bash
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_USERNAME=your_db_user \
  -e DB_PASSWORD=your_db_password \
  loan-api:latest
```

**Note:** For security reasons, it's recommended to provide sensitive credentials via environment variables or Docker secrets rather than hard-coding them in YAML or Dockerfile.

---

## **Default Application Settings**

- **Server Port:** `8080`  
- **Context Path:** `/loan`  
- **Default Profile:** `dev`  
- **H2 Console (dev/test):** `http://localhost:8080/loan/h2-console`  
- **Swagger UI:** `http://localhost:8080/loan/swagger-ui.html`  
- **OpenAPI JSON:** `http://localhost:8080/loan/api-docs`  
- **Pagination Defaults:** `default-page=0`, `default-size=36`  

**JWT Default Settings:**

- **`jwt.secretKey`:** `loan-secret`  
- **`jwt.expirationMs`:** `600000` (10 minutes)

**Connection Pool Configuration:**

```yaml
server:
  port: 8080
  servlet:
    context-path: /loan

spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:test}
  cache:
    type: hazelcast
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000 # 5 minutes
      connection-timeout: 30000 # 30 seconds
      max-lifetime: 1800000 # 30 minutes
  jpa:
    properties:
      hibernate:
        jdbc:
          time_zone: UTC

doc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

app:
  pagination:
    default-page: 0
    default-size: 36

jwt:
  secretKey: ${JWT_SECRET_KEY:loan-secret}
  expirationMs: ${JWT_EXPIRATION_MS:600000}
```

- **Connection Pool Usage:**  
  The application utilizes **HikariCP** for managing database connections efficiently. The pool is configured with a maximum of **20** connections, a minimum of **5** idle connections, and appropriate timeouts to ensure optimal performance and resource management.

---

## **Testing with JaCoCo**

**JaCoCo** is integrated into the project to measure code coverage during testing. It helps ensure that your codebase maintains high quality and that critical paths are well-tested.

### **JaCoCo Configuration in `pom.xml`:**

```xml
<!-- JaCoCo Maven Plugin -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <configuration>
        <excludes>
            <exclude>com/ekiziltan/**/ExcludedPOJO.class</exclude>
            <exclude>com/ekiziltan/**/*DTO.*</exclude>
            <exclude>**/config/*</exclude>
            <exclude>**/entity/*</exclude>
        </excludes>
    </configuration>
    <executions>
        <execution>
            <id>jacoco-initialize</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>jacoco-site</id>
            <phase>package</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <!-- Uncomment the following section to enforce coverage limits -->
        <!--
        <execution>
            <id>check-coverage</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>INSTRUCTION</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
        -->
    </executions>
</plugin>
```

- **Enforcing Coverage Limits:**  
  The commented section in the JaCoCo plugin allows you to enforce coverage limits. By uncommenting this section, you can set minimum coverage ratios for instructions and branches, ensuring that your code maintains a high standard of testing.

### **Generating Coverage Reports:**

After running your tests, JaCoCo generates coverage reports that can be found in the `target/site/jacoco` directory. These reports provide detailed insights into which parts of your codebase are well-tested and which areas may need additional testing.

---

## **Amortization Calculator**

In determining loan installments, banks frequently use the **Amortization Calculator** formula. This formula calculates the fixed monthly payment required to pay off a loan over a specified period, considering both principal and interest.

### **Amortization Formula:**

The standard amortization formula is:

\[
M = P \times \frac{r(1 + r)^n}{(1 + r)^n - 1}
\]

Where:
- \( M \) = Monthly payment
- \( P \) = Principal loan amount
- \( r \) = Monthly interest rate (annual rate divided by 12)
- \( n \) = Total number of payments (loan term in years multiplied by 12)

### **Implementation in the Project:**

The **`InstallmentAmountCalculator`** class utilizes this formula to calculate the final amount for each installment, adjusting for any early payment discounts or late payment penalties.

```java
package com.ekiziltan.loan.service.pay;

import com.ekiziltan.loan.entity.LoanInstallment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class InstallmentAmountCalculator {

    public BigDecimal calculateFinalAmount(LoanInstallment inst, LocalDate now) {
        if (now.isBefore(inst.getDueDate())) {
            return applyDiscount(inst, now);
        } else if (now.isAfter(inst.getDueDate())) {
            return applyPenalty(inst, now);
        }
        return inst.getAmount();
    }

    private BigDecimal applyDiscount(LoanInstallment inst, LocalDate now) {
        long daysEarly = ChronoUnit.DAYS.between(now, inst.getDueDate());
        BigDecimal discount = inst.getAmount()
                .multiply(BigDecimal.valueOf(0.001))
                .multiply(BigDecimal.valueOf(daysEarly));
        return inst.getAmount().subtract(discount);
    }

    private BigDecimal applyPenalty(LoanInstallment inst, LocalDate now) {
        long daysLate = ChronoUnit.DAYS.between(inst.getDueDate(), now);
        BigDecimal penalty = inst.getAmount()
                .multiply(BigDecimal.valueOf(0.001))
                .multiply(BigDecimal.valueOf(daysLate));
        return inst.getAmount().add(penalty);
    }
}
```

- **Discount Calculation:**  
  If the payment is made before the due date, a discount is applied based on the number of days early.
  
- **Penalty Calculation:**  
  If the payment is made after the due date, a penalty is added based on the number of days late.

---

## **Lock Mechanisms and Concurrency Control**

To ensure data integrity and prevent race conditions, especially in a **multi-pod** (or multi-instance) deployment environment, the application employs robust **lock mechanisms**.

### **Locking During Loan Application and Installment Payments:**

- **Pessimistic Locking:**  
  The application uses **pessimistic locks** to prevent multiple transactions from modifying the same data simultaneously. This is crucial when multiple requests attempt to apply for a loan or pay installments concurrently.

- **Transactional Annotations:**  
  Methods that perform critical operations, such as loan applications and installment payments, are annotated with `@Transactional`. This ensures that all operations within the method are executed within a single transaction, maintaining atomicity and consistency.

### **Handling Concurrent Requests:**

- **Loan Application Lock:**  
  When a customer applies for a new loan, the system checks if there are any **in-progress installment payments**. If such payments exist, the loan application is **locked** to prevent conflicts.

- **Installment Payment Lock:**  
  Similarly, when paying installments, the system ensures that no other payment processes are modifying the same loan concurrently by acquiring a lock.

### **Implementation Snippets:**

**Checking and Creating Locks:**

```java
Long customerId = securityHelper.getCustomerIdFromSecurityContext();
installmentPaymentLockService.checkLockExists(customerId);
loanApplicationLockService.createLock(request.getCustomerId());
```

**Security Configuration:**

```java
package com.ekiziltan.loan.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/customer/**").hasAnyRole("ADMIN", "CUSTOMER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
```

**Transactional and Lock Annotations in Service Layer:**

```java
@Override
@Transactional
@PreAuthorize("hasRole('ADMIN') or @loanOwnerSecurityService.isLoanOwner(#request.loanId, principal.customerId)")
@CacheEvict(value = {"loansCache", "installmentsCache"}, allEntries = true)
public PayInstallmentResponse execute(PayInstallmentRequest request) {
    lockService.createLock(request.getLoanId());
    // ... method implementation ...
}
```

- **`@Transactional`:** Ensures that all database operations within the method are executed within a single transaction.
- **`@PreAuthorize`:** Secures the method, allowing only authorized users to execute it.
- **`@CacheEvict`:** Clears relevant caches to maintain data consistency after transactions.

---

## **Connection Pool Configuration**

Efficient management of database connections is vital for application performance and scalability. This project uses **HikariCP**, a high-performance JDBC connection pool.

### **HikariCP Settings in `application.yml`:**

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000 # 5 minutes
      connection-timeout: 30000 # 30 seconds
      max-lifetime: 1800000 # 30 minutes
```

- **`maximum-pool-size`:** Maximum number of connections in the pool.  
- **`minimum-idle`:** Minimum number of idle connections maintained by the pool.  
- **`idle-timeout`:** Time (in milliseconds) after which idle connections are removed.  
- **`connection-timeout`:** Maximum time (in milliseconds) to wait for a connection from the pool.  
- **`max-lifetime`:** Maximum lifetime (in milliseconds) of a connection in the pool.

**Benefits of Using HikariCP:**

- **High Performance:** HikariCP is known for its speed and reliability.
- **Efficient Resource Management:** Proper configuration ensures optimal usage of database resources.
- **Scalability:** Easily handles increased load by adjusting pool sizes.

---

## **Customer Credit Limit Management**

The application manages the **customer's credit limit** based on loan installment payments, focusing solely on the **principal portion** of each payment.

### **Credit Limit Adjustment:**

- **Principal-Based Adjustment:**  
  When a customer pays a loan installment, only the **principal portion** of that payment is **subtracted** from the customer's `usedCreditLimit`. Interest and penalties do not affect the credit usage.

- **Example:**
  - **Loan Installment Amount:** \$1,000  
  - **Principal Portion:** \$800  
  - **Interest/Penalty Portion:** \$200  
  - **Effect on `usedCreditLimit`:** Subtract \$800

### **Service Implementation:**

```java
private void updateCustomerUsedCredit(Loan loan, BigDecimal totalPrincipalPaid) {
    Customer customer = loan.getCustomer();
    if (customer != null && totalPrincipalPaid.compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal newUsedCredit = customer.getUsedCreditLimit().subtract(totalPrincipalPaid);
        // Prevent negative usedCreditLimit
        customer.setUsedCreditLimit(newUsedCredit.max(BigDecimal.ZERO));
        customerRepository.save(customer);
    }
}
```

- **Logic:**  
  Subtracts the total principal paid from the customer's `usedCreditLimit`, ensuring it doesn't drop below zero.

---

## **Database Initialization**

The project includes a **DatabaseInitializer** that populates the database with initial users upon application startup if the database is empty.

### **DatabaseInitializer Class:**

```java
package com.ekiziltan.loan.config.security;

import com.ekiziltan.loan.entity.Customer;
import com.ekiziltan.loan.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            admin.setUsername("admin"); // login username
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

            log.info(">>> 1 Admin + 2 Customers have been created!");
        } else {
            log.error(">>> DB is already initialized!");
        }
    }
}
```

### **Initial Users:**

1. **Admin User:**
   - **Name:** Ali Veli
   - **Username:** `admin`
   - **Password:** `admin123`
   - **Role:** `ROLE_ADMIN`
   - **Credit Limit:** \$100,000
   - **Used Credit Limit:** \$0

2. **Customer Users:**
   - **Customer 1:**
     - **Name:** Ayse Fatma
     - **Username:** `customer1`
     - **Password:** `cust123`
     - **Role:** `ROLE_CUSTOMER`
     - **Credit Limit:** \$50,000
     - **Used Credit Limit:** \$0

   - **Customer 2:**
     - **Name:** Mehmet Can
     - **Username:** `customer2`
     - **Password:** `cust456`
     - **Role:** `ROLE_CUSTOMER`
     - **Credit Limit:** \$75,000
     - **Used Credit Limit:** \$0

---

## **Security and Access Control**

The application ensures that **customers can only access and modify their own loans and installments**, enhancing data security and privacy.

### **Security Configuration:**

```java
package com.ekiziltan.loan.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/customer/**").hasAnyRole("ADMIN", "CUSTOMER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
```

### **Access Control Measures:**

- **Role-Based Access:**
  - **Admins** have access to administrative endpoints.
  - **Customers** can access and manage only their own loans and installments.

- **Method-Level Security:**
  - Utilizes `@PreAuthorize` annotations to enforce access rules at the service layer, ensuring that users can only perform actions on their own data.

- **Lock Mechanism Enforcement:**
  - When a customer has an **in-progress installment payment**, new loan applications are **locked** to prevent concurrent modifications.
  - Conversely, if no in-progress payments exist, the lock mechanism ensures that new actions can proceed without hindrance.

---

## **Loan Listing with Specifications**

The **loan listing** feature leverages **Spring Data JPA Specifications** to provide flexible and optional filtering capabilities, enabling customers and admins to retrieve loans based on various criteria.

### **Specifications Used:**

- **Customer ID Filtering:**
  - Retrieve loans belonging to a specific customer.

- **Number of Installments:**
  - Filter loans based on the number of installments.

- **Creation Date Range:**
  - Fetch loans created within a specific date range.

- **Payment Status:**
  - Filter loans based on whether they are fully paid or not.

### **Implementation in Service Layer:**

```java
package com.ekiziltan.loan.service;

import com.ekiziltan.loan.dto.LoanDTO;
import com.ekiziltan.loan.dto.LoanListForCustomerRequest;
import com.ekiziltan.loan.entity.Loan;
import com.ekiziltan.loan.repository.LoanRepository;
import com.ekiziltan.loan.repository.specifications.LoanSpecifications;
import com.ekiziltan.loan.utils.mapper.LoanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LoansListForCustomerServiceImpl implements LoansListForCustomerService {

    private final LoanRepository loanRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN') or #request.customerId == principal.customerId")
    @Cacheable(value = "loansCache", key = "{#request.customerId, #request.pageable.pageNumber, #request.pageable.pageSize, " +
            "#request.numberOfInstallment, #request.createDateFrom, #request.createDateTo, " +
            "#request.isPaid}")
    public List<LoanDTO> execute(LoanListForCustomerRequest request) {
        Specification<Loan> spec = Specification.where(LoanSpecifications.hasCustomerId(request.getCustomerId()));

        spec = getLoanSpecificationOptionalCriterias(request.getNumberOfInstallment(), request.getCreateDateFrom(),
                request.getCreateDateTo(), request.getIsPaid(),
                spec);

        Page<Loan> loanPage = loanRepository.findAll(spec, request.getPageable());
        return new ArrayList<>(loanPage.map(LoanMapper::entityToDTO).getContent());
    }

    private static Specification<Loan> getLoanSpecificationOptionalCriterias(Integer numberOfInstallment, LocalDate createDateFrom, LocalDate createDateTo, Boolean isPaid, Specification<Loan> spec) {

        if (numberOfInstallment != null) {
            spec = spec.and(LoanSpecifications.hasNumberOfInstallment(numberOfInstallment));
        }
        if (createDateFrom != null && createDateTo != null) {
            spec = spec.and(LoanSpecifications.hasCreateDateBetween(createDateFrom, createDateTo));
        } else if (createDateFrom != null) {
            spec = spec.and(LoanSpecifications.hasCreateDateAfterOrEqual(createDateFrom));
        } else if (createDateTo != null) {
            spec = spec.and(LoanSpecifications.hasCreateDateBeforeOrEqual(createDateTo));
        }

        if (isPaid != null) {
            spec = spec.and(LoanSpecifications.hasIsPaid(isPaid));
        }
        return spec;
    }
}
```

- **Flexible Filtering:**  
  Users can filter loans based on multiple optional criteria, making the loan listing highly customizable and user-friendly.

---

## **Unit Testing Strategy**

To ensure the reliability and maintainability of the application, **unit tests** are implemented across different layers of the application. However, instead of writing exhaustive tests for every single component, the project focuses on providing **example tests for each major layer**, which can serve as a template for further testing.

### **Testing Approach:**

1. **Controller Layer:**
   - Example tests to verify endpoint responses and request handling.

2. **Service Layer:**
   - Example tests to validate business logic and service methods.

3. **Repository Layer:**
   - Example tests to ensure correct data retrieval and persistence.

4. **Security Layer:**
   - Example tests to confirm that security configurations and access controls are enforced correctly.

### **Benefits of This Approach:**

- **Efficiency:**  
  Provides a foundation for testing without overwhelming the development process with too many tests.

- **Scalability:**  
  Easily extendable by following the provided examples to cover additional components as needed.

- **Maintainability:**  
  Ensures that critical paths are tested while allowing flexibility in expanding the test suite over time.

---


### **Entities:**

1. **Customer**
   - **Attributes:** `id`, `name`, `surname`, `username`, `password`, `role`, `creditLimit`, `usedCreditLimit`
   - **Relationships:**
     - One **Customer** can have multiple **Loans**

2. **Loan**
   - **Attributes:** `id`, `loanAmount`, `principalAmount`, `interestAmount`, `numberOfInstallment`, `interestRate`, `createDate`, `isPaid`
   - **Relationships:**
     - Each **Loan** is associated with one **Customer**
     - One **Loan** can have multiple **LoanInstallments**

3. **LoanInstallment**
   - **Attributes:** `id`, `amount`, `paidAmount`, `dueDate`, `paymentDate`, `isPaid`, `principalPortion`, `interestPortion`, `installmentInterestRate`
   - **Relationships:**
     - Each **LoanInstallment** is associated with one **Loan**

4. **InstallmentPaymentLock**
   - **Attributes:** `id`, `loanId`, `customerId`, `status`, `createdDate`, `updatedDate`

5. **LoanApplicationLock**
   - **Attributes:** `id`, `customerId`, `status`, `createdDate`, `updatedDate`

---

## **Contribution and License**

- Feel free to submit **pull requests** or **issues** to contribute to the project.  
- If no explicit license is provided, please adhere to standard copyright.

---

## **Summary**

This **Loan API** project can be **cloned** and **run** directly by following these steps:

1. **Clone the Repository:**  
   ```bash
   git clone https://github.com/username/loan-api.git
   cd loan-api
   ```

2. **Build:**  
   ```bash
   mvn clean package
   ```

3. **Run (Development):**  
   ```bash
   java -jar target/loan-api-1.0.0.jar --spring.profiles.active=test
   ```
   or
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=test
   ```

4. **Access the Application:**  
   - **Swagger UI:** `http://localhost:8080/loan/swagger-ui.html`  
   - **H2 Console (dev/test):** `http://localhost:8080/loan/h2-console`

5. **Docker (Optional):**  
   ```bash
   docker build -t loan-api:latest .
   docker run -d -p 8080:8080 loan-api:latest
   ```

### **Additional Highlights:**

- **Code Coverage:**  
  Utilize **JaCoCo** to monitor and maintain high code coverage standards. Adjust coverage limits as needed by configuring the JaCoCo plugin in `pom.xml`.

- **Amortization Formula:**  
  The project employs the **Amortization Calculator** to determine fixed monthly payments, balancing principal and interest over the loan term.

- **Concurrency Control:**  
  Implemented **pessimistic locking** and **transactional annotations** to manage concurrent loan applications and installment payments, ensuring data integrity in multi-pod deployments.

- **Connection Pooling:**  
  Configured **HikariCP** for efficient database connection management, enhancing application performance and scalability.

- **Credit Limit Management:**  
  Customers' `usedCreditLimit` is dynamically adjusted based on the **principal portion** of their installment payments, ensuring accurate credit usage tracking.

- **Database Initialization:**  
  On the first run, the application initializes the database with **one admin** and **two customer** users for testing and administrative purposes.

- **Security Measures:**  
  Ensures that **customers can only access and modify their own loans and installments**, with robust role-based access controls enforced through **Spring Security**.

- **Flexible Loan Listing:**  
  Uses **Spring Data JPA Specifications** to allow customers and admins to filter loan listings based on various optional criteria, enhancing the usability and functionality of the application.

**Now your project is ready for both **test** and **production** environments.** If you have any questions or want to contribute, feel free to open an **issue** or submit a **pull request** in the repository.

Enjoy your **Loan API**!

---

**Thank you!**  

If you encounter any issues, please open an [issue](https://github.com/username/loan-api/issues) or contribute via a pull request.
