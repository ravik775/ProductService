package org.bgm.productservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ApplicationLifecycleConfig {

    @Bean
    public ApplicationRunner applicationRunner() {

        return args -> {
            log.info("Product Service started successfully");
        };
    }
}