package io.kotlinovsky.restaurant.rent

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "Заказы и меню", version = "1.0"))
@ComponentScan(basePackages = ["io.kotlinovsky.restaurant.core", "io.kotlinovsky.restaurant.auth", "io.kotlinovsky.restaurant.rent"])
open class RentApplication

fun main(args: Array<String>) {
    SpringApplication.run(RentApplication::class.java, *args)
}
