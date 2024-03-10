package com.restaurant.daos

import com.restaurant.infrastructure.query
import com.restaurant.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater

class OrderDao {
    private fun mapRow(row: ResultRow) = Order(
        id = row[Orders.id],
        userId = row[Orders.userId],
        positions = row[Orders.positions],
        status = row[Orders.status],
        createdTimestamp = row[Orders.createdTimestamp]
    )

    suspend fun get(id: Long): Order? = query {
        Orders
            .selectAll().where(Orders.id eq id)
            .map { mapRow(it) }
            .singleOrNull()
    }

    suspend fun getByUserId(userId: Long): List<Order> = query {
        Orders
            .selectAll().where(Orders.userId eq userId)
            .map { mapRow(it) }
    }

    suspend fun create(creatorId: Long, orderDto: OrderPositionsDto): Order = query {
        val result = Orders.insert {
            it[userId] = creatorId
            it[positions] = orderDto.positions
            it[status] = OrderStatus.Created
            it[createdTimestamp] = System.currentTimeMillis()
        }.resultedValues!!.first()

        mapRow(result)
    }


    suspend fun updateStatus(orderId: Long, newStatus: OrderStatus) = query {
        Orders.update ({ Orders.id eq orderId }) {
            it[status] = newStatus
        }
    }

    suspend fun setPositions(orderId: Long, newPositions: List<Long>) = query {
        Orders.update ({ Orders.id eq orderId }) {
            it[positions] = newPositions
        }
    }
}