package io.kotlinovsky.restaurant.auth.resolvers

import io.kotlinovsky.restaurant.auth.annotations.NeedTokenInfo
import io.kotlinovsky.restaurant.auth.exceptions.InvalidCredentialsException
import io.kotlinovsky.restaurant.auth.exceptions.NotEnoughPermissionsException
import io.kotlinovsky.restaurant.auth.managers.TokenManager
import io.kotlinovsky.restaurant.auth.models.TokenOwnerInfo
import io.kotlinovsky.restaurant.auth.models.UserRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * Ресолвер аргумента владельца токена.
 * Осуществляет проверку роли владельца токена, а также
 * передает информацию о владельце в методы, где требуется подтвежденная авторизация.
 */
@Component
class TokenOwnerInfoResolver @Autowired constructor(
    private val tokenManager: TokenManager,
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(param: MethodParameter): Boolean {
        return param.parameterType == TokenOwnerInfo::class.java && param.parameterAnnotations.find {
            it.annotationClass == NeedTokenInfo::class
        } != null
    }

    override fun resolveArgument(
        parameters: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val token = webRequest.getHeader(HttpHeaders.AUTHORIZATION)?.replace("Bearer ", "")
            ?: throw InvalidCredentialsException()
        val ownerInfo = tokenManager.decodeTokenInfo(token) ?: throw InvalidCredentialsException()
        val annotation = parameters
            .parameterAnnotations
            .find { it.annotationClass == NeedTokenInfo::class } as NeedTokenInfo

        if (annotation.needRole != UserRole.CUSTOMER && annotation.needRole != ownerInfo.userRole) {
            throw NotEnoughPermissionsException()
        }

        return ownerInfo
    }
}
