package io.kotlinovsky.restaurant.gateway.configs

import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Конфигурация Swagger.
 * Отвечает за прописывание документаций к микросервисам.
 */
@Configuration
open class SwaggerConfig {

    @Bean
    open fun authApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .pathsToMatch("/auth/**")
            .displayName("Авторизация")
            .group("service-auth")
            .build()
    }

    @Bean
    open fun ordersApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .pathsToMatch("/orders/**")
            .displayName("Заказы и меню")
            .group("service-orders")
            .build()
    }
}
