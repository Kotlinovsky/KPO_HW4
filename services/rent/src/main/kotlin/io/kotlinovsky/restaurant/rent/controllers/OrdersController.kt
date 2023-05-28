package io.kotlinovsky.restaurant.rent.controllers

import io.kotlinovsky.restaurant.auth.annotations.NeedTokenInfo
import io.kotlinovsky.restaurant.auth.models.TokenOwnerInfo
import io.kotlinovsky.restaurant.auth.models.UserRole
import io.kotlinovsky.restaurant.rent.dtos.OrderDishDto
import io.kotlinovsky.restaurant.rent.dtos.OrderDto
import io.kotlinovsky.restaurant.rent.exceptions.OrderNotFoundException
import io.kotlinovsky.restaurant.rent.models.Order
import io.kotlinovsky.restaurant.rent.models.OrderDish
import io.kotlinovsky.restaurant.rent.models.OrderStatus
import io.kotlinovsky.restaurant.rent.services.OrdersService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

/**
 * Контроллер запросов к логике заказов.
 * Осуществляет обработку запросов клиентов к информации о заказах.
 */
@Tag(name = "Заказы")
@RestController
class OrdersController @Autowired constructor(
    private val ordersService: OrdersService,
) {

    @ResponseBody
    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Создать заказ",
        security = [SecurityRequirement(name = "bearerAuth")],
        responses = [
            ApiResponse(responseCode = "201", description = "Заказ успешно создан."),
            ApiResponse(responseCode = "400", description = "Ошибка валидации передаваемых данных.", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Для доступа к методу требуется авторизация.", content = [Content()]),
        ]
    )
    fun createOrder(
        @Parameter(hidden = true) @NeedTokenInfo(needRole = UserRole.CUSTOMER) tokenOwnerInfo: TokenOwnerInfo,
        @Valid @RequestBody orderDto: OrderDto
    ): OrderDto {
        return ordersService
            .createOrder(orderDto.toModel(tokenOwnerInfo.userId))
            .toDto()
    }

    @ResponseBody
    @GetMapping("order/{id}")
    @Operation(
        summary = "Получить заказ",
        responses = [
            ApiResponse(responseCode = "200", description = "ОК"),
            ApiResponse(responseCode = "400", description = "Заказ не существует", content = [Content()])
        ]
    )
    fun getOrder(@PathVariable("id") id: Long): OrderDto {
        return ordersService.getOrder(id)?.toDto() ?: throw OrderNotFoundException()
    }

    private fun OrderDto.toModel(userId: Long): Order {
        return Order(
            id = 0L,
            userId = userId,
            status = OrderStatus.WAITING,
            specialRequest = this.specialRequest,
            dishes = this.dishes.map { it.toModel() }
        )
    }

    private fun OrderDishDto.toModel(): OrderDish {
        return OrderDish(
            id = 0L,
            dishId = this.dishId,
            quantity = this.quantity,
            price = BigDecimal.ZERO,
        )
    }

    private fun Order.toDto(): OrderDto {
        return OrderDto(
            id = this.id,
            status = this.status,
            specialRequest = this.specialRequest,
            dishes = this.dishes.map { it.toDto() }
        )
    }

    private fun OrderDish.toDto(): OrderDishDto {
        return OrderDishDto(
            id = this.id,
            price = this.price,
            dishId = this.dishId,
            quantity = this.quantity
        )
    }
}
