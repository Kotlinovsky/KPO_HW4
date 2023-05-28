package io.kotlinovsky.restaurant.auth.services

import io.kotlinovsky.restaurant.auth.exceptions.EmailBusiedException
import io.kotlinovsky.restaurant.auth.exceptions.InvalidCredentialsException
import io.kotlinovsky.restaurant.auth.exceptions.NicknameBusiedException
import io.kotlinovsky.restaurant.auth.managers.TokenManager
import io.kotlinovsky.restaurant.auth.models.UserRole
import io.kotlinovsky.restaurant.auth.models.User
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

/**
 * Black-box тесты для [AuthService].
 */
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
internal class AuthServiceBlackBoxTest {

    @Autowired
    lateinit var authService: AuthService
    @Autowired
    lateinit var tokenManager: TokenManager

    @Test
    fun throwsNicknameBusiedExceptionWhenNicknameBusied() = runTest {
        authService.signUp(
            nickname = "nickname",
            email = "email1@email.ru",
            password = "password1",
            role = UserRole.CUSTOMER
        )

        assertThrows<NicknameBusiedException> {
            authService.signUp(
                nickname = "nickname",
                email = "email2@email.ru",
                password = "password2",
                role = UserRole.CUSTOMER
            )
        }
    }

    @Test
    fun throwsEmailBusiedExceptionWhenNicknameBusied() = runTest {
        authService.signUp(
            nickname = "nickname1",
            email = "email@email.ru",
            password = "password1",
            role = UserRole.CUSTOMER
        )

        assertThrows<EmailBusiedException> {
            authService.signUp(
                nickname = "nickname2",
                email = "email@email.ru",
                password = "password2",
                role = UserRole.CUSTOMER
            )
        }
    }

    @Test
    fun throwsInvalidCredentialsExceptionWhenSignInFailed() = runTest {
        assertThrows<InvalidCredentialsException> {
            authService.signIn(
                email = "email@email.ru",
                password = "password"
            )
        }
    }

    @Test
    fun returnsTokenAfterSuccesfullySignIn() = runTest {
        authService.signUp(nickname = "nickname", email = "email@email.ru", password = "password", role = UserRole.CUSTOMER)
        assertTrue(authService.signIn(email = "email@email.ru", password = "password").isNotEmpty())
    }

    @Test
    fun returnsNullIfUserWithIdNotFound() = runTest {
        assertNull(authService.getUser(-1L))
    }

    @Test
    fun returnsModelIfUserWithTokenFound() = runTest {
        authService.signUp(nickname = "nickname", email = "email@email.ru", password = "password", role = UserRole.CUSTOMER)

        val token = authService.signIn(email = "email@email.ru", password = "password")
        val user = authService.getUser(tokenManager.decodeTokenInfo(token)!!.userId)
        assertThat(user).usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(User(id = 1L, nickname = "nickname", email = "email@email.ru", role = UserRole.CUSTOMER))
    }
}
