package io.kotlinovsky.restaurant.auth.models

/**
 * Модель с информацией о владельце токене.
 * @property userId ID пользователя, которому был выдан токен.
 * @property userName Имя пользователя, которому был выдан токен.
 * @property userRole Роль пользователя, которому был выдан токен.
 */
data class TokenOwnerInfo(
    val userId: Long,
    val userName: String,
    val userRole: UserRole
)
