package org.bgm.productservice.once;

import org.bgm.productservice.security.models.User;
import org.bgm.productservice.security.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class CreateFirstUserTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void addUser(){

        User user = new User();
        user.setUsername("ravik775@gmail.com");
        user.setPassword(passwordEncoder.encode("Test1234"));
        user.setEnabled(true);
        user.setAccountExpired(false);
        user.setCredentialsExpired(false);
        user.setAccountLocked(false);
        user.setCreatedBy("System");
        user.setUpdatedBy("System");
        String[] roles = {"Admin"};
        user.setRoles(roles);
        repository.save(user);
    }
}
