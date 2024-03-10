package com.restaurant.routes

import com.restaurant.daos.UserDao
import com.restaurant.models.RegisterCredentials
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt

fun Route.auth(
    userDao: UserDao
) {
    route("/api/auth") {
        post("/register") {
            val credentials = call.receive<RegisterCredentials>()

            val userWithSameUsername = userDao.getByUsername(credentials.username)
            if (userWithSameUsername != null) {
                call.respond(HttpStatusCode.BadRequest, "User with same username exists")
                return@post
            }

            val password = BCrypt.hashpw(credentials.password, BCrypt.gensalt())

            userDao.create(credentials.username, password, credentials.isAdmin)
            call.respond(HttpStatusCode.Created)
        }
    }
}