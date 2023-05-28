package io.kotlinovsky.restaurant.auth.configs

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Конфиг службы безопасности сервиса.
 * Отвечает за предоставление зависимостей, связанных
 * с разделением доступа к методам сервиса, с хешированием пароля и т.д.
 */
@Configuration
open class SecurityConfig {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    open fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
