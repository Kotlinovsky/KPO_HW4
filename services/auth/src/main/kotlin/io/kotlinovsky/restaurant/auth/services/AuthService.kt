package io.kotlinovsky.restaurant.auth.services

import io.kotlinovsky.restaurant.auth.exceptions.EmailBusiedException
import io.kotlinovsky.restaurant.auth.exceptions.InvalidCredentialsException
import io.kotlinovsky.restaurant.auth.exceptions.NicknameBusiedException
import io.kotlinovsky.restaurant.auth.models.UserRole
import io.kotlinovsky.restaurant.auth.models.User

/**
 * Интерфейс сервиса авторизации.
 * Описывает функционал работы с пользователя и их сессиями.
 */
interface AuthService {
    /**
     * Регистрирует пользователя, используя переданные в аргументах данные.
     * @param nickname Никнейм (имя) пользователя.
     * @param email E-mail пользователя.
     * @param password Пароль пользователя.
     * @param role Роль пользователя.
     * @throws NicknameBusiedException Когда переданный никнейм уже занят.
     * @throws EmailBusiedException Когда переданный E-mail уже занят.
     */
    @Throws(NicknameBusiedException::class, EmailBusiedException::class)
    fun signUp(nickname: String, email: String, password: String, role: UserRole)

    /**
     * Создает сессию в случае совпадения данных для входа.
     * @param email E-mail пользователя.
     * @param password Пароль пользователя.
     * @return Токен сессии пользователя.
     * @throws InvalidCredentialsException Когда не найден пользователь с переданными данными для входа.
     */
    @Throws(InvalidCredentialsException::class)
    fun signIn(email: String, password: String): String

    /**
     * Ищет пользователя с соответствующим ID.
     * @param id ID искомого пользователя.
     * @return Инстанс модели пользователя. Если null, то пользователь с таким ID не найден.
     */
    fun getUser(id: Long): User?
}
