package com.restaurant.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.math.BigDecimal

object Positions: Table() {
    val id = long("id").autoIncrement()
    val name = text("name")
    val description = text("description")
    val price = double("price")
    val amount = integer("amount")
    val difficulty = integer("difficulty")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Position (
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val amount: Int,
    val difficulty: Int
)

@Serializable
data class PositionDto (
    val name: String,
    val description: String,
    val price: Double,
    val amount: Int,
    val difficulty: Int
)

