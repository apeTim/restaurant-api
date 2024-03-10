package com.restaurant.daos

import com.restaurant.infrastructure.query
import com.restaurant.models.User
import com.restaurant.models.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class UserDao {
    private fun mapRow(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username],
        passwordHash = row[Users.passwordHash],
        isAdmin = row[Users.isAdmin]
    )

    suspend fun get(id: Long): User? = query {
        Users
            .selectAll().where(Users.id eq id)
            .map { mapRow(it) }
            .singleOrNull()
    }

    suspend fun getByUsername(username: String): User? = query {
        Users
            .selectAll().where(Users.username eq username)
            .map { mapRow(it) }
            .singleOrNull()
    }

    suspend fun create(username: String, passwordHash: String, isAdmin: Boolean) = query {
        Users.insert {
            it[Users.username] = username
            it[Users.passwordHash] = passwordHash
            it[Users.isAdmin] = isAdmin
        }
    }
}