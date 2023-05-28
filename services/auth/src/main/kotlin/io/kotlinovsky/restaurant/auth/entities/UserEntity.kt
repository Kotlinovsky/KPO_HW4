package io.kotlinovsky.restaurant.auth.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SourceType
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Date

/**
 * Сущность пользователя в базе данных.
 * @property id ID пользователя.
 * @property username Имя пользователя (никнейм).
 * @property email E-mail пользователя.
 * @property passwordHash Хэш пароля пользователя.
 * @property role Название роли пользователя.
 * @property createdAt Время регистрации пользователя.
 * @property updatedAt Время обновления пользователя.
 */
@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    @Column(name = "username", length = 50)
    val username: String = "",
    @Column(name = "email", length = 100)
    val email: String = "",
    @Column(name = "password_hash", length = 255)
    val passwordHash: String = "",
    @Column(name = "role", length = 10)
    val role: String = "",
    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at")
    val createdAt: Date? = null,
    @UpdateTimestamp(source = SourceType.DB)
    @Column(name = "updated_at")
    val updatedAt: Date? = null,
)
