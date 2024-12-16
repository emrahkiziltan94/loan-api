package com.ekiziltan.loan.config.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    public static final String KEY_CUSTOMER_ID = "customerId";
    public static final String KEY_ROLE = "role";
    public static final String KEY_AUTHORIZATION = "Authorization";
    public static final String PREFIX_TOKEN_BEARER_ = "Bearer ";
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = parseToken(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Claims claims = jwtTokenProvider.getClaims(token);
                String username = claims.getSubject();
                Long customerId = claims.get(KEY_CUSTOMER_ID, Long.class);
                String role = claims.get(KEY_ROLE, String.class);

                CustomerPrincipal principal = new CustomerPrincipal(customerId, username, null, role);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String parseToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(KEY_AUTHORIZATION);
        if (headerAuth != null && headerAuth.startsWith(PREFIX_TOKEN_BEARER_)) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
