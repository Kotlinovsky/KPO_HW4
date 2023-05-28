package io.kotlinovsky.restaurant.auth

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.context.annotation.ComponentScan

@OpenAPIDefinition(info = Info(title = "Авторизация", version = "1.0"))
@ComponentScan(basePackages = ["io.kotlinovsky.restaurant.core", "io.kotlinovsky.restaurant.auth"])
@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
open class AuthApplication

fun main(args: Array<String>) {
    SpringApplication.run(AuthApplication::class.java, *args)
}
