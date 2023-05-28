package io.kotlinovsky.restaurant.auth.controllers

import io.kotlinovsky.restaurant.auth.annotations.NeedTokenInfo
import io.kotlinovsky.restaurant.auth.dtos.SignInRequest
import io.kotlinovsky.restaurant.auth.dtos.SignInResponse
import io.kotlinovsky.restaurant.auth.dtos.SignUpRequest
import io.kotlinovsky.restaurant.auth.dtos.UserDto
import io.kotlinovsky.restaurant.auth.exceptions.InvalidCredentialsException
import io.kotlinovsky.restaurant.auth.models.TokenOwnerInfo
import io.kotlinovsky.restaurant.auth.models.UserRole
import io.kotlinovsky.restaurant.auth.services.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * Контроллер запросов к сервису авторизации.
 * Осуществляет обработку запросов клиентов к сервису.
 */
@Tag(name = "Авторизация")
@RestController
@RequestMapping("auth")
open class AuthController @Autowired constructor(
    private val authService: AuthService
) {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("sign_up")
    @Operation(
        summary = "Зарегистрировать пользователя", responses = [
            ApiResponse(responseCode = "204", description = "Пользователь успешно зарегистрирован."),
            ApiResponse(responseCode = "400", description = "Ошибка валидации передаваемых данных."),
            ApiResponse(responseCode = "409", description = "E-mail и/или никнейм уже заняты."),
        ]
    )
    fun signUp(@Valid @RequestBody request: SignUpRequest) {
        authService.signUp(
            email = request.email,
            nickname = request.username,
            password = request.password,
            role = UserRole.CUSTOMER
        )
    }

    @PostMapping("sign_in")
    @Operation(
        summary = "Авторизовать пользователя",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Пользователь успешно авторизован.",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SignInResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Неправильный логин и/или пароль.",
                useReturnTypeSchema = false,
                content = [Content()],
            ),
        ]
    )
    fun signIn(@Valid @RequestBody request: SignInRequest): SignInResponse {
        return SignInResponse(authService.signIn(request.email, request.password))
    }

    @ResponseBody
    @GetMapping("session")
    @Operation(summary = "Получить текущего пользователя сессии", responses = [
        ApiResponse(
            responseCode = "200",
            description = "ОК",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = UserDto::class)
            )]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Токен не действителен",
            content = [Content()]
        ),
    ], security = [SecurityRequirement(name = "bearerAuth")])
    fun getCurrentSession(@Parameter(hidden = true) @NeedTokenInfo ownerInfo: TokenOwnerInfo): UserDto {
        val user = authService.getUser(ownerInfo.userId) ?: throw InvalidCredentialsException()

        return UserDto(
            id = user.id,
            email = user.email,
            nickname = user.nickname,
            role = user.role.name
        )
    }
}
