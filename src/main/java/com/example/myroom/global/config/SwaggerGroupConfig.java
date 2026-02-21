package com.example.myroom.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class SwaggerGroupConfig {
    
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("인증 & 회원")
                .pathsToMatch("/api/auth/**", "/api/members/**")
                .build();
    }

    @Bean
    public GroupedOpenApi model3dApi() {
        return GroupedOpenApi.builder()
                .group("3D 모델")
                .pathsToMatch("/api/model3ds/**")
                .build();
    }

    @Bean
    public GroupedOpenApi recommendApi() {
        return GroupedOpenApi.builder()
                .group("AI 추천")
                .pathsToMatch("/api/recommands/**")
                .build();
    }

    @Bean
    public GroupedOpenApi postApi() {
        return GroupedOpenApi.builder()
                .group("게시글")
                .pathsToMatch("/api/posts/**", "/api/comments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("관리자")
                .pathsToMatch("/api/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi testDataApi() {
        return GroupedOpenApi.builder()
                .group("🧪 테스트 데이터")
                .pathsToMatch("/api/test-data/**")
                .build();
    }

}
