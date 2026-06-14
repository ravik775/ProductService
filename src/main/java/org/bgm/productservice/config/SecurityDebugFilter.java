package org.bgm.productservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class SecurityDebugFilter extends OncePerRequestFilter {

    public SecurityDebugFilter() {
        System.out.println("SecurityDebugFilter CREATED");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.info("URI      : {}", request.getRequestURI());
            log.info("Auth     : {}", auth);
            log.info("AuthType : {}", auth != null ? auth.getClass() : null);
            log.info("Name     : {}", auth != null ? auth.getName() : null);
            log.info("Authenticated : {}", auth != null && auth.isAuthenticated());
        } else {
            System.out.println("Authentication is NULL");
        }
        filterChain.doFilter(request, response);
    }
}