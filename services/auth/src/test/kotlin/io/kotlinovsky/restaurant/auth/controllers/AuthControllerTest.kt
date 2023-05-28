package io.kotlinovsky.restaurant.auth.controllers

import io.kotlinovsky.restaurant.auth.dtos.SignInRequest
import io.kotlinovsky.restaurant.auth.dtos.SignInResponse
import io.kotlinovsky.restaurant.auth.dtos.SignUpRequest
import io.kotlinovsky.restaurant.auth.models.UserRole
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers.any
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * Тесты для [AuthController].
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
internal class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun returnsErrorIfEmailInInvalidFormatDuringSignUp() {
        val resource = SignUpRequest("username", "email", "password", UserRole.CUSTOMER)

        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resource))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", `is`("Invalid email format!")))
    }

    @Test
    fun returnsErrorIfEmailLongerThan100SymbolsDuringSignUp() {
        val email = "email@1.ru" + "u".repeat(100)
        val resource = SignUpRequest("username", email, "password", UserRole.CUSTOMER)

        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resource))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", `is`("Invalid email format!")))
    }

    @Test
    fun returnsErrorIfNicknameInInvalidFormatDuringSignUp() {
        val resource = SignUpRequest("", "email@email.ru", "password", UserRole.CUSTOMER)

        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resource))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", `is`("Invalid username format!")))
    }

    @Test
    fun returnsErrorIfNicknameLongerThan50SymbolsDuringSignUp() {
        val nickname = "a".repeat(51)
        val resource = SignUpRequest(nickname, "email@email.ru", "password", UserRole.CUSTOMER)

        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resource))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", `is`("Invalid username format!")))
    }

    @Test
    fun returnsErrorIfPasswordInInvalidFormatDuringSignUp() {
        val resource = SignUpRequest("username", "email@email.ru", "", UserRole.CUSTOMER)

        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resource))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", `is`("Invalid password format!")))
    }

    @Test
    fun returnsErrorIfEmailIsBusyDuringSignUp() {
        val resourceFirst = SignUpRequest("username1", "email@email.ru", "password", UserRole.CUSTOMER)

        // Зарегистрируем пользователя.
        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resourceFirst))
                    .contentType(MediaType.APPLICATION_JSON)
            )

        // Теперь зарегистрируем такого же пользователя.
        val resourceSecond = SignUpRequest("username2", "email@email.ru", "password", UserRole.CUSTOMER)
        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resourceSecond))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isConflict)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", `is`("Email is already busy.")))
    }

    @Test
    fun returnsErrorIfNicknameIsBusyDuringSignUp() {
        val resourceFirst = SignUpRequest("username", "email1@email.ru", "password", UserRole.CUSTOMER)

        // Зарегистрируем пользователя.
        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resourceFirst))
                    .contentType(MediaType.APPLICATION_JSON)
            )

        // Теперь зарегистрируем такого же пользователя.
        val resourceSecond = SignUpRequest("username", "email2@email.ru", "password", UserRole.CUSTOMER)
        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resourceSecond))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isConflict)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", `is`("Nickname is already busy.")))
    }

    @Test
    fun returnsCreatedCodeIfSignUpCompleted() {
        val resourceFirst = SignUpRequest("username", "email1@email.ru", "password", UserRole.CUSTOMER)

        // Зарегистрируем пользователя.
        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resourceFirst))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun returnsUnauthorizedCodeWhenCredentialsInvalid() {
        val resource = SignInRequest("email@email.ru", "password")

        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_in")
                    .content(Json.encodeToString(resource))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", `is`("Invalid credentials.")))
    }

    @Test
    fun returnsValidTokenWhenCredentialsValid() {
        // Зарегистрируем пользователя.
        val resourceFirst = SignUpRequest("username", "email@email.ru", "password", UserRole.CUSTOMER)
        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resourceFirst))
                    .contentType(MediaType.APPLICATION_JSON)
            )

        // Теперь попробуем авторизоваться.
        val resourceSecond = SignInRequest("email@email.ru", "password")
        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_in")
                    .content(Json.encodeToString(resourceSecond))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
    }

    @Test
    fun returnsUserInfoIfTokenValid() {
        // Зарегистрируем пользователя.
        val resourceFirst = SignUpRequest("username", "email@email.ru", "password", UserRole.CUSTOMER)
        mockMvc
            .perform(
                post("http://localhost:8080/auth/sign_up")
                    .content(Json.encodeToString(resourceFirst))
                    .contentType(MediaType.APPLICATION_JSON)
            )

        // Авторизируем пользователя.
        val resourceSecond = SignInRequest("email@email.ru", "password")
        val signInResult = Json.decodeFromString<SignInResponse>(
            mockMvc
                .perform(
                    post("http://localhost:8080/auth/sign_in")
                        .content(Json.encodeToString(resourceSecond))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .response
                .contentAsString
        )

        // Попробуем запросить информацию о пользователе.
        mockMvc
            .perform(
                get("http://localhost:8080/auth/session")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${signInResult.token}")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", any(Int::class.java)))
            .andExpect(jsonPath("$.nickname", `is`("username")))
            .andExpect(jsonPath("$.email", `is`("email@email.ru")))
            .andExpect(jsonPath("$.role", `is`("CUSTOMER")))
    }

    @Test
    fun returnsUnauthorizedIfTokenNotReceived() {
        mockMvc
            .perform(
                get("http://localhost:8080/auth/session")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized)
    }
}
