package io.kotlinovsky.restaurant.core.exceptions

import org.springframework.http.HttpStatus

/**
 * Абстрактный класс, описывающий ошибку API.
 * Такие ошибки обрабатываются собственной логикой, а не логикой фреймворка Spring.
 * @property status Статус ответа сервера.
 * @property description Описание возникшей ошибки.
 */
abstract class ApiException(
    val status: HttpStatus,
    val description: String
) : RuntimeException()
