package io.kotlinovsky.restaurant.rent.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SourceType
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Date

/**
 * Сущность заказа пользователя.
 * @property id ID заказа.
 * @property userId ID заказчика.
 * @property status Статус заказа.
 * @property specialRequest Специальный запрос заказчика.
 * @property createdAt Время создания заказа.
 * @property updatedAt Время обновления заказа.
 */
@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    @Column(name = "user_id")
    val userId: Long? = 0L,
    @Column(name = "status")
    val status: String = "",
    @Column(name = "special_request")
    val specialRequest: String = "",
    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at")
    val createdAt: Date? = null,
    @UpdateTimestamp(source = SourceType.DB)
    @Column(name = "updated_at")
    val updatedAt: Date? = null,
)
