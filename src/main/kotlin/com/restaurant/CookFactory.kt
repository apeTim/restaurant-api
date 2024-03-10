package com.restaurant

import com.restaurant.daos.OrderDao
import com.restaurant.daos.PositionDao

class CookFactory(private val orderDao: OrderDao, private val positionDao: PositionDao) {
    fun createCook(orderId: Long): Cook {
        return Cook(orderId, orderDao, positionDao)
    }
}