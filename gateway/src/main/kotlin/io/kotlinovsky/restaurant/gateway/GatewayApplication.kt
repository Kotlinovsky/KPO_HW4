package io.kotlinovsky.restaurant.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class GatewayApplication

fun main(args: Array<String>) {
    SpringApplication.run(GatewayApplication::class.java, *args)
}
