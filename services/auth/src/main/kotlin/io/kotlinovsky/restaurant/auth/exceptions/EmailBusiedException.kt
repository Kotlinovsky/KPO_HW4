package io.kotlinovsky.restaurant.auth.exceptions

import io.kotlinovsky.restaurant.core.exceptions.ApiException
import org.springframework.http.HttpStatus

/**
 * Исключение занятости E-mail (электронной почты).
 * Выбрасывается в случаях, когда происходит попытка
 * смены/указания E-mail на E-mail, который уже занят.
 */
class EmailBusiedException : ApiException(HttpStatus.CONFLICT, "Email is already busy.")
