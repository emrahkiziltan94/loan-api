package com.ekiziltan.loan.config.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String KEY_CUSTOMER_ID = "customerId";
    public static final String KEY_ROLE = "role";
    @Value("${jwt.secretKey}")
    private String secretKeyString;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String sKey = Base64.getEncoder().encodeToString(key.getEncoded());
        this.secretKey = Keys.hmacShaKeyFor(sKey.getBytes());
    }

    public String generateToken(Long customerId, String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim(KEY_CUSTOMER_ID, customerId)
                .claim(KEY_ROLE, role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {

            log.error("JWT token validation failed", e);
        }
        return false;
    }

    public Claims getClaims(String token) {

        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
