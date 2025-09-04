package com.example.swnuclearfusionwas.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "시니링 API 명세서")
)
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi api() {
        String[] paths = {"/**"};
        return GroupedOpenApi.builder()
                .group("OAuth2 & JWT API")
                .pathsToMatch(paths)
                .build();
    }
}
