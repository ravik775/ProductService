package org.bgm.productservice.security.services;

import lombok.extern.slf4j.Slf4j;
import org.bgm.productservice.security.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("Loading details for {}", username);
        var user = userRepository.findByUsername(username);
        if(user.isPresent())
        {
            var dbUser = user.get();
            var userDetails = org.springframework.security.core.userdetails.User
                    .builder()
                    .username(dbUser.getUsername())
                    .password(dbUser.getPassword())
                    .accountExpired(dbUser.isAccountExpired())
                    .accountLocked(dbUser.isAccountLocked())
                    .disabled(!dbUser.isEnabled())
                    .credentialsExpired(dbUser.isCredentialsExpired())
                    .authorities(dbUser.getRoles())
                    .build();
            log.info("User {} details retrived.", username);
            return userDetails;
        }
        log.info("User {} is not a valid user", username);
        throw new UsernameNotFoundException("User "+ username+" is not a valid user");
    }
}

/*

1. User submits login form
        |
        v
2. UsernamePasswordAuthenticationFilter
        |
        v
3. AuthenticationManager
        |
        v
4. DaoAuthenticationProvider
        |
        +--> UserDetailsService
        |        |
        |        v
        |    Database
        |
        +--> PasswordEncoder.matches()
        |
        v
5. Authentication Success
        |
        v
6. JWT Token Generated

 */