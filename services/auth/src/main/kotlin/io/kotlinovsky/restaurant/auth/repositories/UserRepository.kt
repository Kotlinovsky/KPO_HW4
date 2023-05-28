package io.kotlinovsky.restaurant.auth.repositories

import io.kotlinovsky.restaurant.auth.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Репозиторий для работы с [UserEntity].
 * Осуществляет операции с данными пользователей.
 */
@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    /**
     * Проверяет, существует ли пользователь с переданным никнеймом.
     * @param username Никнейм (имя) пользователя.
     * @return True - если существует, false - если не существует.
     */
    fun existsByUsername(username: String): Boolean

    /**
     * Проверяет, существует ли пользователь с таким e-mail.
     * @param email E-mail (электронная почта) пользователя.
     * @return True - если существует, false - если не существует.
     */
    fun existsByEmail(email: String): Boolean

    /**
     * Ищет пользователя по его e-mail.
     * @param email E-mail (электронная почта) пользователя.
     * @return Сущность пользователя из БД. Если null, то пользователь не найден.
     */
    fun findByEmail(email: String): UserEntity?
}
