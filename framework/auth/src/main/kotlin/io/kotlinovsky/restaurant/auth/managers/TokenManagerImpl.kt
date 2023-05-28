package io.kotlinovsky.restaurant.auth.managers

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.kotlinovsky.restaurant.auth.models.TokenOwnerInfo
import io.kotlinovsky.restaurant.auth.models.UserRole
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

/**
 * Реализация [TokenManager].
 * Осуществляет генерацию и декодирование JWT-токенов
 * с помощью библиотеки JsonWebToken.
 */
@Component
internal class TokenManagerImpl(
    @Qualifier("private_token_key") private val privateTokenKey: Key,
    @Qualifier("public_token_key") private val publicTokenKey: Key
) : TokenManager {

    private val tokenParser = Jwts.parserBuilder()
        .setSigningKey(publicTokenKey)
        .setClock { Date.from(Instant.now()) }
        .build()

    override fun decodeTokenInfo(token: String): TokenOwnerInfo? {
        try {
            val claims = tokenParser.parseClaimsJws(token).body
            val userId = (claims[USER_ID] as? Long ?: claims[USER_ID] as? Int)?.toLong() ?: return null
            val userRole = (claims[USER_ROLE] as? String)?.let { UserRole.valueOf(it) } ?: return null
            val userName = claims.subject ?: return null

            return TokenOwnerInfo(
                userId = userId,
                userRole = userRole,
                userName = userName
            )
        } catch (ex: JwtException) {
            return null
        }
    }

    override fun encodeTokenInfo(tokenInfo: TokenOwnerInfo): String {
        val expiresAt = Date.from(ZonedDateTime.now().plusYears(1).toInstant())

        return Jwts.builder()
            .setIssuer("Restaurant")
            .setSubject(tokenInfo.userName)
            .claim(USER_ID, tokenInfo.userId)
            .claim(USER_ROLE, tokenInfo.userRole.name)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(expiresAt)
            .signWith(privateTokenKey)
            .compact()
    }

    private companion object {
        const val USER_ID = "user_id"
        const val USER_ROLE = "user_role"
    }
}
