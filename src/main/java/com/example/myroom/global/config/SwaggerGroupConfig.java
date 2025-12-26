package com.example.myroom.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class SwaggerGroupConfig {
    
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("일반 API")
                .pathsToMatch("/api/**")
                .pathsToExclude("/api/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("Admin API")
                .pathsToMatch("/api/admin/**")
                .build();
    }

}
