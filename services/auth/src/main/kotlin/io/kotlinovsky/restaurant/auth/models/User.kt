package io.kotlinovsky.restaurant.auth.models

/**
 * Модель зарегистрированного пользователя.
 * @property id ID пользователя.
 * @property nickname Никнейм (имя) пользователя.
 * @property email E-mail, указанный пользователем.
 * @property role Роль пользователя.
 */
data class User(
    val id: Long,
    val nickname: String,
    val email: String,
    val role: UserRole,
)
