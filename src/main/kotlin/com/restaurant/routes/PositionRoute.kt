package com.restaurant.routes

import com.restaurant.daos.PositionDao
import com.restaurant.models.PositionDto
import com.restaurant.models.RegisterCredentials
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.positions(
    positionDao: PositionDao
) {
    route("/api/positions") {
        authenticate("auth") {
            get("/available") {
                val positions = positionDao.getAvailable()
                call.respond(positions)
            }
        }

        authenticate("admin-auth") {
            post {
                val position = call.receive<PositionDto>()
                positionDao.create(position)

                call.respond(HttpStatusCode.Created)
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalStateException("Numeric ID required")
                val existingPosition = positionDao.get(id)

                if (existingPosition == null) {
                    call.respond(HttpStatusCode.NotFound, "Position not found")
                    return@put
                }

                val position = call.receive<PositionDto>()
                positionDao.update(id, position)

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}