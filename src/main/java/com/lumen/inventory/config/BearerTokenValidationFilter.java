package com.lumen.inventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to validate the presence of a bearer token header for all requests.
 * The header name is configurable via naas.bearer.token.validation.header property.
 */
@Component
public class BearerTokenValidationFilter extends OncePerRequestFilter {

    @Value("${naas.bearer.token.validation.header}")
    private String naasBearerTokenValidationHeader;

    @Override
    protected void doFilterInternal(
            @org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(naasBearerTokenValidationHeader);

        if (token == null || token.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Missing or empty authorization header\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}