package com.qualityinspection.swequalityinspection.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
public class JacksonConfig {

    @Bean
    public EnumModule enumModule() {
        return new EnumModule();
    }
}
