package io.kotlinovsky.restaurant.auth.managers

import io.kotlinovsky.restaurant.auth.models.TokenOwnerInfo
import io.kotlinovsky.restaurant.auth.models.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

/**
 * Тесты для [TokenManagerImpl].
 */
class TokenManagerImplTest {

    private val keyPairFactory = KeyPairGenerator.getInstance("EC").apply { initialize(384) }
    private val secondKeyPair = keyPairFactory.generateKeyPair()
    private val privateTokenKey: Key by lazy {
        val key = PRIVATE_PEM.replace("\n", "")
        val encoded = Base64.getDecoder().decode(key.toByteArray())
        val keyFactory = KeyFactory.getInstance("EC")
        val keySpec = PKCS8EncodedKeySpec(encoded)
        keyFactory.generatePrivate(keySpec)
    }

    private val publicTokenKey: Key by lazy {
        val key = PUBLIC_PEM.replace("\n", "")
        val encoded = Base64.getDecoder().decode(key.toByteArray())
        val keyFactory = KeyFactory.getInstance("EC")
        val keySpec = X509EncodedKeySpec(encoded)
        keyFactory.generatePublic(keySpec)
    }

    private val firstTokenManager = TokenManagerImpl(privateTokenKey = privateTokenKey, publicTokenKey = publicTokenKey)
    private val secondTokenManager = TokenManagerImpl(
        privateTokenKey = secondKeyPair.private,
        publicTokenKey = secondKeyPair.public
    )

    @Test
    fun returnsNullIfTokenNotSign() {
        val tokenInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = secondTokenManager.encodeTokenInfo(tokenInfo)
        assertNull(firstTokenManager.decodeTokenInfo(token))
    }

    @Test
    fun returnsInfoIfAllDataWrittenAndSignatureNotBroken() {
        val tokenInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = firstTokenManager.encodeTokenInfo(tokenInfo)
        assertEquals(tokenInfo, firstTokenManager.decodeTokenInfo(token))
    }

    private companion object {
        const val PRIVATE_PEM = "MIG2AgEAMBAGByqGSM49AgEGBSuBBAAiBIGeMIGbAgEBBDCL0JJ/FdvuLOXoQQLT\n" +
                "zp7MDHHL/avQpcKEGeR9B7Us7I1pViWXEx7EVP6V4ZXmpHqhZANiAASh5UPHrS0D\n" +
                "ZT6tLUySs77ytozoftkvo8Ba2RWGdoHFvQ3bJ01bOq1rSkw3mKYZRxqPhbCmAptI\n" +
                "/x9DACRVfbPj6Ws0AfLPs8/W/Ptz5hey/WgS+EBdABl4ZcW6CDNxA1s="
        const val PUBLIC_PEM = "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAEoeVDx60tA2U+rS1MkrO+8raM6H7ZL6PA\n" +
                "WtkVhnaBxb0N2ydNWzqta0pMN5imGUcaj4WwpgKbSP8fQwAkVX2z4+lrNAHyz7PP\n" +
                "1vz7c+YXsv1oEvhAXQAZeGXFuggzcQNb"
    }
}
