package io.kotlinovsky.restaurant.auth.repositories

import io.kotlinovsky.restaurant.auth.entities.SessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Репозиторий для работы с [SessionEntity].
 * Осуществляет хранение сессий пользователей.
 */
@Repository
interface SessionRepository : JpaRepository<SessionEntity, Long>
