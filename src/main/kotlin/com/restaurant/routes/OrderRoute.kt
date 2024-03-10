package com.restaurant.routes

import com.restaurant.CookFactory
import com.restaurant.daos.OrderDao
import com.restaurant.daos.PositionDao
import com.restaurant.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

fun Route.orders(
    orderDao: OrderDao,
    positionDao: PositionDao
) {
    route("/api/orders") {
        val cookFactory = CookFactory(orderDao, positionDao)

        authenticate("auth") {
            get("my") {
                val user = call.principal<UserRolePrincipal>() ?: throw IllegalStateException("Incorrect user id")

                val orders = orderDao.getByUserId(user.id)

                call.respond(orders)
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalStateException("Numeric ID required")
                val order = orderDao.get(id)

                if (order == null) {
                    call.respond(HttpStatusCode.NotFound, "Order not found")
                    return@get
                }

                call.respond(order)
            }

            post("/{id}/add-positions") {
                val (order, error) = getOrderAndVerify(call, orderDao, listOf(OrderStatus.Created, OrderStatus.Preparing))

                if (order == null) {
                    call.respond(HttpStatusCode.BadRequest, error ?: "Unknown error")
                    return@post
                }

                val newPositions = call.receive<OrderPositionsDto>().positions
                val existingPositions = positionDao.get(newPositions)

                if (existingPositions.size != newPositions.size) {
                    call.respond(HttpStatusCode.BadRequest, "Some positions does not exist")
                    return@post
                }

                val concatPositions = order.positions + newPositions
                orderDao.setPositions(order.id, concatPositions)
                call.respond(HttpStatusCode.OK)
            }

            post("/{id}/cancel") {
                val (order, error) = getOrderAndVerify(call, orderDao, listOf(OrderStatus.Created, OrderStatus.Preparing))

                if (order == null) {
                    call.respond(HttpStatusCode.BadRequest, error ?: "Unknown error")
                    return@post
                }

                orderDao.updateStatus(order.id, OrderStatus.Cancelled)
                call.respond(HttpStatusCode.OK)
            }

            post("/{id}/pay") {
                val (order, error) = getOrderAndVerify(call, orderDao, listOf(OrderStatus.WaitingPayment))

                if (order == null) {
                    call.respond(HttpStatusCode.BadRequest, error ?: "Unknown error")
                    return@post
                }

                orderDao.updateStatus(order.id, OrderStatus.Finished)
                call.respond(HttpStatusCode.OK)
            }

            post {
                val order = call.receive<OrderPositionsDto>()
                val existingPositions = positionDao.get(order.positions)

                if (existingPositions.size != order.positions.size) {
                    call.respond(HttpStatusCode.BadRequest, "Some positions does not exist")
                    return@post
                }

                val user = call.principal<UserRolePrincipal>() ?: throw IllegalStateException("Incorrect user id")
                val createdOrder = orderDao.create(user.id, order)

                val cook = cookFactory.createCook(createdOrder.id)
                launch { cook.start() }
                call.respond(HttpStatusCode.Created)
            }
        }
    }
}

suspend fun getOrderAndVerify(call: ApplicationCall, orderDao: OrderDao, allowedStatuses: List<OrderStatus>): Pair<Order?, String?> {
    val user = call.principal<UserRolePrincipal>() ?: throw IllegalStateException("Incorrect user id")
    val id = call.parameters["id"]?.toLong() ?: throw IllegalStateException("Numeric ID required")
    val order = orderDao.get(id) ?: return Pair(null, "Order not found")

    if (user.id != order.userId)
        return Pair(null, "Only order owner can call this operation")

    if (!allowedStatuses.contains(order.status))
        return Pair(null, "Order has incorrect status for this operation")

    return Pair(order, null)
}
