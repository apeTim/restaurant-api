package com.restaurant.daos

import com.restaurant.infrastructure.query
import com.restaurant.models.Position
import com.restaurant.models.PositionDto
import com.restaurant.models.Positions
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

class PositionDao {
    private fun mapRow(row: ResultRow) = Position(
        id = row[Positions.id],
        name = row[Positions.name],
        description = row[Positions.description],
        price = row[Positions.price],
        amount = row[Positions.amount],
        difficulty = row[Positions.difficulty]
    )

    suspend fun get(id: Long): Position? = query {
        Positions
            .selectAll().where(Positions.id eq id)
            .map { mapRow(it) }
            .singleOrNull()
    }

    suspend fun get(ids: List<Long>): List<Position> = query {
        Positions
            .selectAll().where(Positions.id inList ids)
            .map { mapRow(it) }
    }

    suspend fun getAvailable(): List<Position> = query {
        Positions
            .selectAll().where(Positions.amount greater 0)
            .map { mapRow(it) }
    }

    suspend fun create(positionDto: PositionDto) = query {
        Positions.insert {
            it[name] = positionDto.name
            it[description] = positionDto.description
            it[amount] = positionDto.amount
            it[price] = positionDto.price
            it[difficulty] = positionDto.difficulty
        }
    }

    suspend fun update(id: Long, positionDto: PositionDto) = query {
        Positions
            .update ({ Positions.id eq id }) {
                it[name] = positionDto.name
                it[description] = positionDto.description
                it[amount] = positionDto.amount
                it[price] = positionDto.price
                it[difficulty] = positionDto.difficulty
            }
    }

    suspend fun delete(id: Long) = query {
        Positions
            .deleteWhere { Positions.id eq id }
    }
}