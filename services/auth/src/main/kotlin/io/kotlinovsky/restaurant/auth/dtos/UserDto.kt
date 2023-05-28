package io.kotlinovsky.restaurant.auth.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO пользователя системы.
 * @property id ID пользователя.
 * @property nickname Никнейм пользователя.
 * @property email Электронная почта пользователя.
 * @property role Роль пользователя.
 */
@Serializable
data class UserDto(
    @SerialName("id")
    val id: Long,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("email")
    val email: String,
    @SerialName("role")
    val role: String
)
