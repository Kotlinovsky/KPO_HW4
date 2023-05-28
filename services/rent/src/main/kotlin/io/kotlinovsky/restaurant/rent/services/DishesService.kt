package io.kotlinovsky.restaurant.rent.services

import io.kotlinovsky.restaurant.rent.exceptions.DishNotFoundException
import io.kotlinovsky.restaurant.rent.models.Dish
import kotlin.jvm.Throws

/**
 * Интерфейс сервиса управления блюдами.
 * Описывает действия, которые можно осуществлять с блюдами.
 */
interface DishesService {
    /**
     * Добавляет блюдо в систему.
     * Реализация может игнорировать установленный ID в передаваемой модели.
     * @param dish Инстанс модели блюда, которое будет добавлено в систему.
     * @return Инстанс модели блюда с назначенным реализацией ID.
     */
    fun addDish(dish: Dish): Dish

    /**
     * Обновляет в системе блюдо с указанным в модели ID.
     * @param dish Инстанс модели блюда с новыми данными.
     * @throws DishNotFoundException Блюда с таким ID нет в системе.
     */
    @Throws(DishNotFoundException::class)
    fun updateDish(dish: Dish)

    /**
     * Удаляет блюдо с указанным ID из системы.
     * @param dishId ID удаляемого из системы блюда.
     * @throws DishNotFoundException Блюда с таким ID нет в системе.
     */
    @Throws(DishNotFoundException::class)
    fun deleteDish(dishId: Long)

    /**
     * Получает модель блюда с указанным ID.
     * @param dishId ID искомого в системе блюда.
     * @return Инстанс модели блюда. Если null, то значит, что блюда с таким ID нет в системе.
     */
    fun getDish(dishId: Long): Dish?

    /**
     * Возвращает список блюд, существующих в системе.
     * @return Список с моделями блюд в системе.
     */
    fun getDishes(): List<Dish>

    /**
     * Возвращает список блюд с указанными ID.
     * @param ids Список с ID искомых блюд.
     * @return Список с найденными блюдами.
     */
    fun getDishesByIds(ids: List<Long>): List<Dish>
}
