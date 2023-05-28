package io.kotlinovsky.restaurant.core.configs

import kotlinx.serialization.json.Json
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Конфигурация веб-части сервиса.
 * Отвечает за предоставление зависимостей конвертеров, сериализаторов и т.д.
 */
@Configuration
open class WebConfig : WebMvcConfigurer {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    open fun messageConverter(): KotlinSerializationJsonHttpMessageConverter {
        return KotlinSerializationJsonHttpMessageConverter(Json {
            ignoreUnknownKeys = true
        })
    }
}
