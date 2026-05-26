package org.bgm.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RESTTemplateConfig {

    @Bean
    public RestTemplate getRESTTemplate(){
        return new RestTemplate();
    }

    @Bean
    public WebClient webClient() {
        /*
        Yes, multiple Spring Boot starters can coexist. When both starter-web and starter-webflux are present, Spring Boot defaults to Spring MVC/Tomcat, while WebFlux is commonly included only to use WebClient in otherwise traditional applications.
         */
        return WebClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }
}
