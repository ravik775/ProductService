package org.bgm.productservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupVersionListener {

    private static final Logger log =
            LoggerFactory.getLogger(StartupVersionListener.class);

    @EventListener(ApplicationReadyEvent.class)
    public void printVersions() {

        log.info("========================================");
        logVersion("Spring Boot",
                org.springframework.boot.SpringApplication.class);

        logVersion("Spring Framework",
                org.springframework.core.SpringVersion.class);

        logVersion("Spring Security",
                org.springframework.security.web.FilterChainProxy.class);

        logVersion("Authorization Server",
                org.springframework.security.oauth2.server.authorization.OAuth2Authorization.class);

        logVersion("Jackson",
                com.fasterxml.jackson.databind.ObjectMapper.class);

        log.info("========================================");
    }

    private void logVersion(String name, Class<?> clazz) {

        Package pkg = clazz.getPackage();

        String version = pkg.getImplementationVersion();

        log.info("{} : {}",
                name,
                version != null ? version : "Unknown");
    }
}