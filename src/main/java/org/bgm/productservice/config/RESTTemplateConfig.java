package org.bgm.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RESTTemplateConfig {

    @Bean
    public RestTemplate getRESTTemplate(){
        return new RestTemplate();
    }
}
