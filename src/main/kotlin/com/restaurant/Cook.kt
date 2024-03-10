package com.restaurant

import com.restaurant.daos.OrderDao
import com.restaurant.daos.PositionDao
import com.restaurant.models.OrderStatus
import kotlinx.coroutines.delay

class Cook(private val orderId: Long, private val orderDao: OrderDao, private val positionDao: PositionDao) {
    suspend fun start() {
        println("Starting to prepare order #${orderId}")
        orderDao.updateStatus(orderId, OrderStatus.Preparing)
        var doneDifficulty = 0
        var difficulty = getDifficulty()

        while (doneDifficulty < difficulty) {
            val difference = difficulty - doneDifficulty
            println("Preparing #$orderId with difficulty $difference")
            delay(difference.toLong())
            doneDifficulty = difficulty

            difficulty = getDifficulty()
        }

        println("Order #$orderId is prepared")
        orderDao.updateStatus(orderId, OrderStatus.WaitingPayment)
    }

    private suspend fun getDifficulty(): Int {
        val order = orderDao.get(orderId) ?: return 0
        val positionIds = order.positions
        val positionIdsCount: MutableMap<Long, Int> = mutableMapOf()
        for (i in positionIds.indices) {
            val id = positionIds[i]
            if (!positionIdsCount.containsKey(id)) {
                positionIdsCount[id] = 0
            }

            positionIdsCount[id] = positionIdsCount[id]!! + 1
        }


        val positions = positionDao.get(order.positions)
        val difficulty = positions.sumOf { x -> x.difficulty * (positionIdsCount[x.id] ?: 0) }

        return difficulty
    }
}