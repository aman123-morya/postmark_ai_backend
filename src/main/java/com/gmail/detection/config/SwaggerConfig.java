package com.gmail.detection.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exposes interactive API docs at /swagger-ui/index.html (JSON at /v3/api-docs).
 * Both are permitted without auth in SecurityConfig. A "Bearer Auth" scheme is
 * registered so protected endpoints can be tried directly from the Swagger UI
 * by pasting in the access token returned from /api/auth/login.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Gmail Management System API")
                        .description("Enterprise email management platform with Gemini-powered "
                                + "classification, priority routing, spam detection, and smart replies.")
                        .version("v1.0")
                        .contact(new Contact().name("Gmail Management System")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
