package io.kotlinovsky.restaurant.auth.configs

import io.kotlinovsky.restaurant.auth.resolvers.TokenOwnerInfoResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Конфигурация ресолверов аргументов методов.
 * Осуществляет прописывание ресолверов в конфиг Spring MVC.
 */
@Configuration
open class ExternalResolversConfig @Autowired constructor(
    private val tokenOwnerInfoResolver: TokenOwnerInfoResolver
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        super.addArgumentResolvers(resolvers)
        resolvers.add(tokenOwnerInfoResolver)
    }
}
