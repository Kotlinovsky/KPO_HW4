package io.kotlinovsky.restaurant.auth.entities

import jakarta.persistence.*
import java.util.*

/**
 * Сущность сессии пользователя.
 * @property id ID сессии.
 * @property token Токен сессии пользователя.
 * @property user Пользователь, с которым связана сессия.
 * @property expiresAt Время, до которого действительна сессия.
 */
@Entity
@Table(name = "sessions")
data class SessionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = UserEntity::class)
    @JoinColumn(name = "user_id")
    val user: UserEntity = UserEntity(),
    @Column(name = "session_token", length = 1000)
    val token: String = "",
    @Column(name = "expires_at")
    val expiresAt: Date? = null
)
