package com.example.bank_rest_test_task.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация openApi
 */
@Configuration
public class OpenAPIConfig {
    /**
     * Добавление версии и авторизации в openApi документацию
     *
     * @return настроенный openApi
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        .addParameters("page", new Parameter()
                                .in("query")
                                .name("page")
                                .schema(new IntegerSchema()))
                        .addParameters("size", new Parameter()
                                .in("query")
                                .name("size")
                                .schema(new IntegerSchema()))
                        .addParameters("sort", new Parameter()
                                .in("query")
                                .name("sort")
                                .schema(new StringSchema())))
                .info(new Info()
                        .title("Bank system")
                        .version("1.0.0"));
    }
}
