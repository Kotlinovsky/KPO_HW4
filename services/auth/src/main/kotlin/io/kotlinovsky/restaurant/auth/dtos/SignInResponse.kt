package io.kotlinovsky.restaurant.auth.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO ответа сервера в случае успешного завершения авторизации.
 * @property token Токен сессии пользователя.
 */
@Serializable
data class SignInResponse(
    @SerialName("token")
    val token: String
)
