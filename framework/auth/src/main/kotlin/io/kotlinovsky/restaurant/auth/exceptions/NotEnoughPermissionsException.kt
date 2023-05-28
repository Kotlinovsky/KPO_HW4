package io.kotlinovsky.restaurant.auth.exceptions

import io.kotlinovsky.restaurant.core.exceptions.ApiException
import org.springframework.http.HttpStatus

/**
 * Исключение недостатка прав для выполнения метода.
 * Выбрасывается, когда роль пользователя не позволяет исполнить тот или иной запрос.
 */
class NotEnoughPermissionsException : ApiException(HttpStatus.FORBIDDEN, "You don't have enough permissions.")
