package io.kotlinovsky.restaurant.rent.controllers

import io.kotlinovsky.restaurant.auth.annotations.NeedTokenInfo
import io.kotlinovsky.restaurant.auth.models.TokenOwnerInfo
import io.kotlinovsky.restaurant.auth.models.UserRole
import io.kotlinovsky.restaurant.rent.dtos.DishDto
import io.kotlinovsky.restaurant.rent.exceptions.DishNotFoundException
import io.kotlinovsky.restaurant.rent.models.Dish
import io.kotlinovsky.restaurant.rent.services.DishesService
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

/**
 * Контроллер запросов к логике блюд.
 * Осуществляет обработку запросов клиентов к информации о блюдах.
 */
@Tag(name = "Блюда")
@RestController
open class DishesController @Autowired constructor(
    private val dishesService: DishesService,
) {

    @ResponseBody
    @PostMapping("dishes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Добавить блюдо",
        security = [SecurityRequirement(name = "bearerAuth")],
        responses = [
            ApiResponse(responseCode = "201", description = "Блюдо успешно добавлено."),
            ApiResponse(responseCode = "400", description = "Ошибка валидации передаваемых данных.", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Для доступа к методу требуется авторизация.", content = [Content()]),
            ApiResponse(responseCode = "403", description = "Данный пользователь не может добавлять блюда.", content = [Content()]),
        ]
    )
    fun addDish(
        @Parameter(hidden = true) @NeedTokenInfo(needRole = UserRole.MANAGER) tokenOwnerInfo: TokenOwnerInfo,
        @Valid @RequestBody dishDto: DishDto
    ): DishDto {
        return dishesService
            .addDish(dishDto.toModel())
            .toDto()
    }

    @ResponseBody
    @PutMapping("dish/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Обновляет информацию о блюде",
        security = [SecurityRequirement(name = "bearerAuth")],
        responses = [
            ApiResponse(responseCode = "204", description = "Блюдо успешно обновлено."),
            ApiResponse(responseCode = "400", description = "Ошибка валидации передаваемых данных."),
            ApiResponse(responseCode = "401", description = "Для доступа к методу требуется авторизация."),
            ApiResponse(responseCode = "403", description = "Данный пользователь не может обновлять блюда."),
            ApiResponse(responseCode = "404", description = "Блюдо не существует."),
        ]
    )
    fun updateDish(
        @Parameter(hidden = true) @NeedTokenInfo(needRole = UserRole.MANAGER) tokenOwnerInfo: TokenOwnerInfo,
        @PathVariable("id") id: Long,
        @Valid @RequestBody dishDto: DishDto
    ) {
        dishesService.updateDish(dishDto.toModel().copy(id = id))
    }

    @ResponseBody
    @DeleteMapping("dish/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Удаляет блюдо",
        security = [SecurityRequirement(name = "bearerAuth")],
        responses = [
            ApiResponse(responseCode = "204", description = "Блюдо успешно удалено."),
            ApiResponse(responseCode = "400", description = "Ошибка валидации передаваемых данных."),
            ApiResponse(responseCode = "401", description = "Для доступа к методу требуется авторизация."),
            ApiResponse(responseCode = "403", description = "Данный пользователь не может удалять блюда."),
            ApiResponse(responseCode = "404", description = "Блюдо не существует."),
        ]
    )
    fun deleteDish(
        @Parameter(hidden = true) @NeedTokenInfo(needRole = UserRole.MANAGER) tokenOwnerInfo: TokenOwnerInfo,
        @PathVariable("id") id: Long
    ) {
        dishesService.deleteDish(id)
    }

    @ResponseBody
    @GetMapping("dish/{id}")
    @Operation(
        summary = "Получает блюдо",
        responses = [
            ApiResponse(responseCode = "200", description = "ОК"),
            ApiResponse(responseCode = "404", description = "Блюдо не существует.", content = [Content()]),
        ]
    )
    fun getDish(@PathVariable("id") id: Long): DishDto {
        return dishesService.getDish(id)?.toDto() ?: throw DishNotFoundException()
    }

    @ResponseBody
    @GetMapping("dishes")
    @Operation(
        summary = "Получает список блюд",
        responses = [ApiResponse(responseCode = "200", description = "ОК") ]
    )
    fun getDishes(): List<DishDto> {
        return dishesService.getDishes().map { it.toDto() }
    }


    @ResponseBody
    @GetMapping("menu")
    @Operation(
        summary = "Получает список блюд, доступных для заказа (меню)",
        responses = [ApiResponse(responseCode = "200", description = "ОК") ]
    )
    fun getMenu(): List<DishDto> {
        return dishesService
            .getDishes()
            .asSequence()
            .filter { it.quantity > 0 }
            .map { it.toDto() }
            .toList()
    }

    private fun DishDto.toModel(): Dish {
        return Dish(
            id = this.id!!,
            name = this.name,
            description = this.description,
            quantity = this.quantity,
            price = this.price,
        )
    }

    private fun Dish.toDto(): DishDto {
        return DishDto(
            id = this.id,
            name = this.name,
            description = this.description,
            quantity = this.quantity,
            price = this.price,
        )
    }
}
