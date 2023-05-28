package io.kotlinovsky.restaurant.auth.services

import io.kotlinovsky.restaurant.auth.entities.SessionEntity
import io.kotlinovsky.restaurant.auth.entities.UserEntity
import io.kotlinovsky.restaurant.auth.exceptions.EmailBusiedException
import io.kotlinovsky.restaurant.auth.exceptions.InvalidCredentialsException
import io.kotlinovsky.restaurant.auth.exceptions.NicknameBusiedException
import io.kotlinovsky.restaurant.auth.managers.TokenManager
import io.kotlinovsky.restaurant.auth.models.TokenOwnerInfo
import io.kotlinovsky.restaurant.auth.models.UserRole
import io.kotlinovsky.restaurant.auth.models.User
import io.kotlinovsky.restaurant.auth.repositories.SessionRepository
import io.kotlinovsky.restaurant.auth.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*

/**
 * Реализация [AuthService].
 * Осуществляет управление пользователями за счет
 * работы с [UserRepository] и [SessionRepository].
 */
@Service
internal class AuthServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenManager: TokenManager,
) : AuthService {

    override fun signUp(nickname: String, email: String, password: String, role: UserRole) {
        if (userRepository.existsByUsername(nickname)) {
            throw NicknameBusiedException()
        } else if (userRepository.existsByEmail(email)) {
            throw EmailBusiedException()
        }

        val passwordHash = passwordEncoder.encode(password)
        val entity = UserEntity(username = nickname, email = email, passwordHash = passwordHash, role = role.name)
        userRepository.save(entity)
    }

    override fun signIn(email: String, password: String): String {
        val user = userRepository.findByEmail(email)
        if (user == null || !passwordEncoder.matches(password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }

        val expiresAt = Date.from(ZonedDateTime.now().plusYears(1).toInstant())
        val ownerInfo = TokenOwnerInfo(userId = user.id!!, userName = user.username, userRole = UserRole.valueOf(user.role))
        val token = tokenManager.encodeTokenInfo(ownerInfo)
        val entity = SessionEntity(user = user, token = token, expiresAt = expiresAt)
        sessionRepository.save(entity)
        return token
    }

    override fun getUser(id: Long): User? {
        return try {
            val user = userRepository.findByIdOrNull(id) ?: return null
            User(id = user.id!!, nickname = user.username, email = user.email, role = UserRole.valueOf(user.role))
        } catch (exception: Exception) {
            null
        }
    }
}
