package io.kotlinovsky.restaurant.core.advices

import io.kotlinovsky.restaurant.core.exceptions.ApiException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URL
import java.util.*

/**
 * Перехватчик ошибок валидации и API ошибок.
 * Осуществляет вывод сообщения о причине ошибки.
 */
@RestControllerAdvice
class ApiErrorsAdvice {

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val error = ex.fieldErrors[0].defaultMessage ?: "Bad request."

        return ResponseEntity
            .badRequest()
            .body(createMap(request, error))
    }

    @ResponseBody
    @ExceptionHandler(ApiException::class)
    fun handleApiError(ex: ApiException, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        return ResponseEntity
            .status(ex.status.value())
            .body(createMap(request, ex.description))
    }

    private fun createMap(request: HttpServletRequest, error: String): Map<String, Any> {
        val path = URL(request.requestURL.toString()).path

        return mapOf(
            "timestamp" to Date(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to error,
            "path" to path
        )
    }
}
