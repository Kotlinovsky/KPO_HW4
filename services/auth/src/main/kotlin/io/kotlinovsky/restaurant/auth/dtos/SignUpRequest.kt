package io.kotlinovsky.restaurant.auth.dtos

import io.kotlinovsky.restaurant.auth.models.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO запроса регистрации пользователя.
 * @property username Никнейм (имя) пользователя.
 * @property email E-mail (электронная почта) пользователя.
 * @property password Пароль пользователя.
 */
@Serializable
data class SignUpRequest(
    @field:NotBlank(message = "Invalid username format!")
    @field:Size(min = 1, max = 50, message = "Invalid username format!")
    @SerialName("username")
    val username: String,
    @field:Email(message = "Invalid email format!")
    @field:Size(min = 1, max = 100, message = "Invalid email format!")
    @SerialName("email")
    val email: String,
    @field:NotBlank(message = "Invalid password format!")
    @SerialName("password")
    val password: String,
    @SerialName("role")
    val role: UserRole
)
