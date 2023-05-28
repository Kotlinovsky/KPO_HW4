package io.kotlinovsky.restaurant.auth.annotations

import io.kotlinovsky.restaurant.auth.models.UserRole

/**
 * Аннотация, обозначающая необходимость получения информации о токене
 * для выполнения логики запроса, а также для указания минимальной роли
 * для доступа к методу.
 * @property needRole Необходимая роль для доступа.
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class NeedTokenInfo(val needRole: UserRole = UserRole.CUSTOMER)
