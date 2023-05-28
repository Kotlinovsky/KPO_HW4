package io.kotlinovsky.restaurant.auth.dtos

import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO запроса авторизации пользователя.
 * @property email E-mail (электронная почта) пользователя.
 * @property password Пароль пользователя.
 */
@Serializable
data class SignInRequest(
    @field:NotBlank(message = "Invalid email format!")
    @SerialName("email")
    val email: String,
    @field:NotBlank(message = "Invalid password format!")
    @SerialName("password")
    val password: String,
)
