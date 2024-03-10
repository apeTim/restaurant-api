package com.restaurant.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Orders: Table() {
    val id = long("id").autoIncrement()
    val userId = long("userId").references(Users.id)
    val positions = array<Long>("positions")
    val status = enumerationByName<OrderStatus>("status", 256)
    val createdTimestamp = long("createdTimestamp")

    override val primaryKey = PrimaryKey(id)
}

enum class OrderStatus(status: String) {
    Created("created"),
    Preparing("preparing"),
    WaitingPayment("waiting-payment"),
    Finished("finished"),
    Cancelled("finished")
}

@Serializable
data class Order (
    val id: Long,
    val userId: Long,
    val positions: List<Long>,
    val status: OrderStatus,
    val createdTimestamp: Long
)

@Serializable
data class OrderPositionsDto (
    val positions: List<Long>
)
