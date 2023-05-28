package io.kotlinovsky.restaurant.rent.controllers

import io.kotlinovsky.restaurant.auth.managers.TokenManager
import io.kotlinovsky.restaurant.auth.models.TokenOwnerInfo
import io.kotlinovsky.restaurant.auth.models.UserRole
import io.kotlinovsky.restaurant.rent.dtos.DishDto
import io.kotlinovsky.restaurant.rent.dtos.OrderDishDto
import io.kotlinovsky.restaurant.rent.dtos.OrderDto
import io.kotlinovsky.restaurant.rent.models.OrderStatus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

/**
 * Тесты для [OrdersController].
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
internal class OrdersControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var tokenManager: TokenManager

    @Test
    fun orderCreationAvailableOnlyForAuthorizedUsers() {
        val orderDto = OrderDto(specialRequest = "", dishes = listOf(OrderDishDto(dishId = 1L, quantity = 1)))
        mockMvc
            .perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(orderDto)))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun orderCreationReturnsErrorIfDishesListEmpty() {
        addTempDish()

        val orderDto = OrderDto(specialRequest = "", dishes = listOf())
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        mockMvc
            .perform(post("/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(orderDto)))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("Dishes list cannot be empty!")))
    }

    @Test
    fun orderCreationReturnErrorIfDishOrderQuantityNegative() {
        addTempDish()

        val orderDto = OrderDto(specialRequest = "", dishes = listOf(OrderDishDto(dishId = 1L, quantity = -1)))
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        mockMvc
            .perform(post("/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(orderDto)))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("Invalid quantity format!")))
    }

    @Test
    fun orderCreationReturnErrorIfDishOrderQuantityEqualsZero() {
        addTempDish()

        val orderDto = OrderDto(specialRequest = "", dishes = listOf(OrderDishDto(dishId = 1L, quantity = 0)))
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        mockMvc
            .perform(post("/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(orderDto)))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("Invalid quantity format!")))
    }

    @Test
    fun orderCreationReturnErrorIfNotExists() {
        val orderDto = OrderDto(specialRequest = "", dishes = listOf(OrderDishDto(dishId = 1L, quantity = 1)))
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        mockMvc
            .perform(post("/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(orderDto)))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("Dishes not enough!")))
    }

    @Test
    fun orderCreationIgnoringIdFromDto() {
        addTempDish()

        val orderDto = OrderDto(id = 10L, specialRequest = "", dishes = listOf(OrderDishDto(dishId = 1L, quantity = 1)))
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val response = Json.decodeFromString<OrderDto>(mockMvc
            .perform(post("/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(orderDto)))
            .andReturn()
            .response
            .contentAsString)

        assertNotEquals(orderDto.id, response.id)
    }

    @Test
    fun orderCreationIgnoringStatusFromDto() {
        addTempDish()

        val orderDto = OrderDto(status = OrderStatus.COMPLETED, specialRequest = "", dishes = listOf(OrderDishDto(dishId = 1L, quantity = 1)))
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val response = Json.decodeFromString<OrderDto>(mockMvc
            .perform(post("/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(orderDto)))
            .andReturn()
            .response
            .contentAsString)

        assertNotEquals(orderDto.status, response.status)
    }

    @Test
    fun orderCreationIgnoringOrderDishIdFromDto() {
        addTempDish()

        val orderDto = OrderDto(specialRequest = "", dishes = listOf(OrderDishDto(id = 10L, dishId = 1L, quantity = 1)))
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val response = Json.decodeFromString<OrderDto>(mockMvc
            .perform(post("/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(orderDto)))
            .andReturn()
            .response
            .contentAsString)

        assertNotEquals(orderDto.dishes[0].id, response.dishes[0].id)
    }

    @Test
    fun orderCreationIgnoringOrderDishPriceFromDto() {
        addTempDish()

        val orderDto = OrderDto(specialRequest = "", dishes = listOf(OrderDishDto(price = BigDecimal.TWO, dishId = 1L, quantity = 1)))
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val response = Json.decodeFromString<OrderDto>(mockMvc
            .perform(post("/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(orderDto)))
            .andReturn()
            .response
            .contentAsString)

        assertNotEquals(orderDto.dishes[0].price, response.dishes[0].price)
    }

    @Test
    fun orderCreatingReturnsOkStatus() {
        addTempDish()

        val orderDto = OrderDto(specialRequest = "", dishes = listOf(OrderDishDto(price = BigDecimal.TWO, dishId = 1L, quantity = 1)))
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)

        mockMvc
            .perform(post("/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(orderDto)))
            .andExpect(status().isCreated)
    }

    @Test
    fun getOrderReturnsValidDtoAndStatus() {
        addTempDish()

        val orderDto = OrderDto(specialRequest = "Request", dishes = listOf(OrderDishDto(dishId = 1L, quantity = 1)))
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.CUSTOMER)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        val added = Json.decodeFromString<OrderDto>(mockMvc
            .perform(post("/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(orderDto)))
            .andReturn()
            .response
            .contentAsString)
        val loaded = Json.decodeFromString<OrderDto>(mockMvc
            .perform(get("/order/${added.id}"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString)

        assertEquals(added, loaded)
    }

    @Test
    fun getOrderReturnsErrorIfItNotExists() {
        mockMvc
            .perform(get("/order/10"))
            .andExpect(status().isNotFound)
    }

    private fun addTempDish() {
        val tokenOwnerInfo = TokenOwnerInfo(userId = 1L, userName = "Name", userRole = UserRole.MANAGER)
        val resource = DishDto(id = 1L, name = "Name", quantity = 1, description = "Description", price = BigDecimal.ONE)
        val token = tokenManager.encodeTokenInfo(tokenOwnerInfo)
        mockMvc.perform(
            post("http://localhost:8080/dishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(resource))
        )
    }
}
