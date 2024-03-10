package com.restaurant.models

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Users: Table() {
    val id = long("id").autoIncrement()
    val username = text("username")
    val passwordHash = text("passwordHash")
    val isAdmin = bool("isAdmin")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class User (
    val id: Long,
    val username: String,
    val passwordHash: String,
    val isAdmin: Boolean
)

data class UserRolePrincipal(val id: Long, val isAdmin: Boolean) : Principal