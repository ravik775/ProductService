package org.bgm.productservice.security;

import lombok.extern.slf4j.Slf4j;
import org.bgm.productservice.security.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthenticationSuccessLogger {

    private final UserRepository userRepository;

    public AuthenticationSuccessLogger(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void logSuccessfulAuthentication(Authentication authentication) {
        String username = authentication.getName();
        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String roles = userRepository.findByUsername(username)
                .map(user -> Arrays.toString(user.getRoles()))
                .orElse("[]");

        log.info("Authentication successful | user={} | roles={} | authorities={}",
                username, roles, authorities);
    }
}
