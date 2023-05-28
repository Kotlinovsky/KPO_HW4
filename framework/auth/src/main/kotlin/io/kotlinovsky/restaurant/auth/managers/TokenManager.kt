package io.kotlinovsky.restaurant.auth.managers

import io.kotlinovsky.restaurant.auth.models.TokenOwnerInfo

/**
 * Интерфейс менеджера токенов пользователей.
 * Описывает операции по декодированию и кодированию токенов.
 */
interface TokenManager {
    /**
     * Декодирует токен и выдает информацию о его владельце.
     * @param token Токен доступа пользователя.
     * @return Информация о токене. Если null, то декодировать токен не удалось.
     */
    fun decodeTokenInfo(token: String): TokenOwnerInfo?

    /**
     * Генерирует JWT-токен и помещает в него информацию о владельце.
     * @param tokenInfo Информация о владельце токена.
     * @return Токен с закодированной внутри него информацией.
     */
    fun encodeTokenInfo(tokenInfo: TokenOwnerInfo): String
}
