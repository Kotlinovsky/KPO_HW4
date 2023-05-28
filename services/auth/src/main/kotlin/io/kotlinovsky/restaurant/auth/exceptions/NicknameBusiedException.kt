package io.kotlinovsky.restaurant.auth.exceptions

import io.kotlinovsky.restaurant.core.exceptions.ApiException
import org.springframework.http.HttpStatus

/**
 * Исключение занятости никнейма.
 * Выбрасывается в случаях, когда происходит попытка
 * смены/указания никнейма на никнейм, который уже занят.
 */
class NicknameBusiedException : ApiException(HttpStatus.CONFLICT, "Nickname is already busy.")
