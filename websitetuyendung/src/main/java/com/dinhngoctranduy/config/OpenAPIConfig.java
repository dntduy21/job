package com.dinhngoctranduy.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI myOpenAPI(@Value("${open.api.title}") String title,
                             @Value("${open.api.version}") String version,
                             @Value("${open.api.description}") String description,
                             @Value("${open.api.serverUrl}") String serverUrl,
                             @Value("${open.api.serverName}") String serverName) {
        return new OpenAPI()
                .info(new Info().title(title)
                        .version(version)
                        .description(description))
                .servers(List.of(new Server().url(serverUrl).description(serverName)))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }
}