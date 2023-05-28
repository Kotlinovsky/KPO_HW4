package io.kotlinovsky.restaurant.core.configs

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

/**
 * Конфигурация Swagger'а.
 */
@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
open class SwaggerConfig
