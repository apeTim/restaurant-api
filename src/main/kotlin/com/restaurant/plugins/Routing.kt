package com.restaurant.plugins

import com.restaurant.daos.OrderDao
import com.restaurant.daos.PositionDao
import com.restaurant.daos.UserDao
import com.restaurant.infrastructure.Database
import com.restaurant.routes.auth
import com.restaurant.routes.orders
import com.restaurant.routes.positions
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }

    Database.init(environment)
    val userDao = UserDao()
    val positionDao = PositionDao()
    val orderDao = OrderDao()

    routing {
        auth(userDao)
        positions(positionDao)
        orders(orderDao, positionDao)
    }
}
