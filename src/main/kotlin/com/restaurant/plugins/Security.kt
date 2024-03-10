package com.restaurant.plugins

import com.restaurant.daos.UserDao
import com.restaurant.models.UserRolePrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt

fun Application.configureSecurity() {
    val userDao = UserDao()

    authentication {
        basic(name = "auth") {
            validate { credentials ->
                val user = userDao.getByUsername(credentials.name)
                if (user != null && BCrypt.checkpw(credentials.password, user.passwordHash)) {
                    UserRolePrincipal(user.id, user.isAdmin)
                } else {
                    null
                }
            }
        }

        basic(name = "admin-auth") {
            validate { credentials ->
                val user = userDao.getByUsername(credentials.name)
                if (user != null && BCrypt.checkpw(credentials.password, user.passwordHash) && user.isAdmin) {
                    UserRolePrincipal(user.id, user.isAdmin)
                } else {
                    null
                }
            }
        }
    }
}
