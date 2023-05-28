package io.kotlinovsky.restaurant.auth.configs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.core.io.Resource
import java.security.Key
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

/**
 * Конфигурация логики токенов.
 * Отвечает за предоставление необходимых зависимостей
 * для генерации и декодирования токенов.
 */
@Configuration
open class TokenConfig @Autowired constructor(
    @Value("classpath:keys/token_private_key.key")
    private val tokenPrivateKeyPem: Resource,
    @Value("classpath:keys/token_public_key.pem")
    private val tokenPublicKeyPem: Resource
) {

    @Bean("private_token_key")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    open fun tokenPrivateKey(): Key {
        val key = tokenPrivateKeyPem
            .getContentAsString(Charsets.UTF_8)
            .replace("\n", "")
        val encoded = Base64.getDecoder().decode(key.toByteArray())
        val keyFactory = KeyFactory.getInstance("EC")
        val keySpec = PKCS8EncodedKeySpec(encoded)
        return keyFactory.generatePrivate(keySpec)
    }

    @Bean("public_token_key")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    open fun tokenPublicKey(): Key {
        val key = tokenPublicKeyPem
            .getContentAsString(Charsets.UTF_8)
            .replace("\n", "")
        val encoded = Base64.getDecoder().decode(key.toByteArray())
        val keyFactory = KeyFactory.getInstance("EC")
        val keySpec = X509EncodedKeySpec(encoded)
        return keyFactory.generatePublic(keySpec)
    }
}
