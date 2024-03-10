package com.restaurant.infrastructure

import com.restaurant.models.Orders
import com.restaurant.models.Positions
import com.restaurant.models.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun createDataSource(driver: String, url: String, username: String, password: String, maximumPoolSize: Int): HikariDataSource {
    val config = HikariConfig()
    config.driverClassName = driver
    config.jdbcUrl = url
    config.username = username
    config.password = password
    config.maximumPoolSize = maximumPoolSize
    config.transactionIsolation = "TRANSACTION_READ_COMMITTED"
    config.isAutoCommit = false

    config.validate()

    return HikariDataSource(config)
}

object Database {
    fun init (environment: ApplicationEnvironment) {
        val driver = environment.config.property("db.driver").getString()
        val url = environment.config.property("db.url").getString()
        val username = environment.config.property("db.user").getString()
        val password = environment.config.property("db.pass").getString()
        val pool = environment.config.property("db.pool").getString().toInt()

        Database.connect(createDataSource(driver, url, username, password, pool))

        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(Positions)
            SchemaUtils.create(Orders)
        }
    }
}

suspend fun<T> query(
    block: () -> T
): T = withContext(Dispatchers.IO) {
    transaction { block() }
}
