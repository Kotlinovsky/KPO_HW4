package io.kotlinovsky.restaurant.rent.controllers

import io.kotlinovsky.restaurant.auth.managers.TokenManager
import io.kotlinovsky.restaurant.auth.models.TokenOwnerInfo
import io.kotlinovsky.restaurant.auth.models.UserRole
import io.kotlinovsky.restaurant.rent.dtos.DishDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

/**
 * Тесты для [DishesController].
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
internal class DishesControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var tokenManager: TokenManager

    @Test
    fun returnsMenuWithOnlyAvailableDishes() {
        // Для начала добавим блюдо в систему.
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val resource = DishDto(id = 1L, name = "Name", quantity = 1, description = "Description", price = BigDecimal.ONE)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val firstDishDto = Json.decodeFromString<DishDto>(mockMvc.perform(
            post("http://localhost:8080/dishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andReturn().response.contentAsString)

        Json.decodeFromString<DishDto>(mockMvc.perform(
            post("http://localhost:8080/dishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource.copy(id = 2L, name = "Second", quantity = 0)))
        ).andReturn().response.contentAsString)

        // Теперь попробуем запросить меню.
        val dishes = Json.decodeFromString<List<DishDto>>(mockMvc
            .perform(get("/menu"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString)
        assertArrayEquals(arrayOf(firstDishDto), dishes.toTypedArray())
    }

    @Test
    fun addingAllowedOnlyForManager() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = "Name", quantity = 1, description = "Description", price = BigDecimal.ONE)
        mockMvc.perform(
            post("http://localhost:8080/dishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun removingAllowedOnlyForManager() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        mockMvc
            .perform(delete("http://localhost:8080/dish/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun updatingAllowedOnlyForManager() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = "Name", quantity = 1, description = "Description", price = BigDecimal.ONE)
        mockMvc.perform(
            put("http://localhost:8080/dish/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun addingReturnsErrorIfNameIsBlank() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = "", quantity = 1, description = "Description", price = BigDecimal.ONE)
        mockMvc.perform(
            post("http://localhost:8080/dishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isBadRequest).andExpect(jsonPath("$.error", `is`("Invalid name format!")))
    }

    @Test
    fun addingReturnsErrorIfNameLongerThan100Symbols() {
        val name = "1".repeat(101)
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = name, quantity = 1, description = "Description", price = BigDecimal.ONE)
        mockMvc.perform(
            post("http://localhost:8080/dishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isBadRequest).andExpect(jsonPath("$.error", `is`("Invalid name format!")))
    }

    @Test
    fun addingReturnsErrorIfPriceNegative() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = "Name", quantity = 1, description = "Description", price = BigDecimal.valueOf(-1))
        mockMvc.perform(
            post("http://localhost:8080/dishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isBadRequest).andExpect(jsonPath("$.error", `is`("Invalid price format!")))
    }

    @Test
    fun addingReturnsErrorIfPriceViolatesScaling() {
        val price = BigDecimal.valueOf(-1, 3)
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = "Name", quantity = 1, description = "Description", price = price)
        mockMvc.perform(
            post("http://localhost:8080/dishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isBadRequest).andExpect(jsonPath("$.error", `is`("Invalid price format!")))
    }

    @Test
    fun addingReturnsErrorIfQuantityNegative() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = "Name", quantity = -1, description = "Description", price = BigDecimal.ONE)
        mockMvc.perform(
            post("http://localhost:8080/dishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isBadRequest).andExpect(jsonPath("$.error", `is`("Invalid quantity format!")))
    }

    @Test
    fun addingIgnoresIdInRequestBody() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(id = 10L, name = "Name", quantity = 1, description = "Description", price = BigDecimal.ONE)
        val result = Json.decodeFromString<DishDto>(
            mockMvc.perform(
                post("http://localhost:8080/dishes")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Json.encodeToString(resource))
            ).andExpect(status().isCreated).andReturn().response.contentAsString
        )

        assertNotEquals(result.id, resource.id)
    }

    @Test
    fun updateReturnsErrorIfDishNotExists() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(id = 10L, name = "Name", quantity = 1, description = "Description", price = BigDecimal.ONE)
        mockMvc.perform(put("http://localhost:8080/dish/1000")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Json.encodeToString(resource)))
            .andExpect(status().isNotFound)
    }

    @Test
    fun updateReturnsErrorIfNameIsBlank() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = "", quantity = 1, description = "Description", price = BigDecimal.ONE)
        mockMvc.perform(
            put("http://localhost:8080/dish/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isBadRequest).andExpect(jsonPath("$.error", `is`("Invalid name format!")))
    }

    @Test
    fun updateReturnsErrorIfNameLongerThan100Symbols() {
        val name = "1".repeat(101)
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = name, quantity = 1, description = "Description", price = BigDecimal.ONE)
        mockMvc.perform(
            put("http://localhost:8080/dish/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isBadRequest).andExpect(jsonPath("$.error", `is`("Invalid name format!")))
    }

    @Test
    fun updateReturnsErrorIfPriceNegative() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = "Name", quantity = 1, description = "Description", price = BigDecimal.valueOf(-1))
        mockMvc.perform(
            put("http://localhost:8080/dish/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isBadRequest).andExpect(jsonPath("$.error", `is`("Invalid price format!")))
    }

    @Test
    fun updateReturnsErrorIfPriceViolatesScaling() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = "Name", quantity = 1, description = "Description", price = BigDecimal.valueOf(1, 3))
        mockMvc.perform(
            put("http://localhost:8080/dish/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isBadRequest).andExpect(jsonPath("$.error", `is`("Invalid price format!")))
    }

    @Test
    fun updateReturnsErrorIfQuantityNegative() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(name = "Name", quantity = -1, description = "Description", price = BigDecimal.ONE)
        mockMvc.perform(
            put("http://localhost:8080/dish/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        ).andExpect(status().isBadRequest).andExpect(jsonPath("$.error", `is`("Invalid quantity format!")))
    }

    @Test
    fun updateIgnoresDishIdInRequestBody() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(id = 1L, name = "Name", quantity = 1, description = "Description", price = BigDecimal.ONE)
        val id = Json.decodeFromString<DishDto>(
            mockMvc.perform(
                post("http://localhost:8080/dishes")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Json.encodeToString(resource)))
            .andReturn()
            .response
            .contentAsString
        ).id

        mockMvc.perform(
            put("http://localhost:8080/dish/${id}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource.copy(id = 10L)))
        ).andExpect(status().isNoContent)

       mockMvc
           .perform(get("http://localhost:8080/dish/${id}"))
           .andExpect(status().isOk)
    }

    @Test
    fun removeReturnsErrorIfDishNotExists() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        mockMvc
            .perform(delete("http://localhost:8080/dish/99")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun removeDeletingDishFromSystem() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(id = 1L, name = "Name", quantity = 1, description = "Description", price = BigDecimal.ONE)
        val id = Json.decodeFromString<DishDto>(
            mockMvc.perform(
                post("http://localhost:8080/dishes")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Json.encodeToString(resource)))
                .andReturn()
                .response
                .contentAsString
        ).id

        mockMvc
            .perform(delete("http://localhost:8080/dish/$id")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token"))
            .andExpect(status().isNoContent)
        mockMvc
            .perform(get("http://localhost:8080/dish/${id}"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getByIdReturnsErrorIfDishNotExists() {
        mockMvc.perform(get("http://localhost:8080/dish/99")).andExpect(status().isNotFound)
    }

    @Test
    fun getByIdReturnsEntityIfDishExists() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(id = 10L, name = "Name", quantity = 1, description = "Description", price = BigDecimal.ONE)
        val added = Json.decodeFromString<DishDto>(
            mockMvc.perform(
                post("http://localhost:8080/dishes")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Json.encodeToString(resource))
            ).andReturn().response.contentAsString
        )
        val loaded = Json.decodeFromString<DishDto>(mockMvc.perform(
            get("http://localhost:8080/dish/${added.id}")
        ).andReturn().response.contentAsString)

        assertEquals(added, loaded)
    }

    @Test
    fun getDishesReturnsDishesList() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val resource = DishDto(id = 10L, name = "Name", quantity = 1, description = "Description", price = BigDecimal.ONE)
        val added = Json.decodeFromString<DishDto>(
            mockMvc.perform(
                post("http://localhost:8080/dishes")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Json.encodeToString(resource))
            ).andReturn().response.contentAsString
        )
        val loaded = Json.decodeFromString<List<DishDto>>(mockMvc.perform(
            get("http://localhost:8080/dishes")
        ).andReturn().response.contentAsString)

        assertArrayEquals(arrayOf(added), loaded.toTypedArray())
    }
}
