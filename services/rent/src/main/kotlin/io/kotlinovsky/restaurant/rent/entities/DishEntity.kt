package io.kotlinovsky.restaurant.rent.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SourceType
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.sql.Date

/**
 * Сущность блюда.
 * @property id ID блюда.
 * @property name Название блюда.
 * @property description Описание блюда.
 * @property quantity Количество доступных единиц блюда.
 * @property price Цена блюда за единицу.
 */
@Entity
@Table(name = "dishes")
data class DishEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @Column(name = "name")
    var name: String = "",
    @Column(name = "description")
    var description: String = "",
    @Column(name = "quantity")
    var quantity: Int = 0,
    @Column(name = "price", precision = 10, scale = 2)
    var price: BigDecimal = BigDecimal.ZERO,
    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at")
    val createdAt: Date? = null,
    @UpdateTimestamp(source = SourceType.DB)
    @Column(name = "updated_at")
    val updatedAt: Date? = null,
)
