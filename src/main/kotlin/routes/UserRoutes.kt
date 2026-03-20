package org.delcom.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.delcom.services.UserService
import org.delcom.helpers.JWTConstants

fun Route.userRoutes(service: UserService) {
    authenticate(JWTConstants.NAME) {
        route("/users") {
            // Ambil data saya sendiri
            get("/me") {
                service.getMe(call)
            }

            // Update profil
            put("/me") {
                service.putMe(call)
            }

            // Update foto profil
            put("/me/photo") {
                service.putMyPhoto(call)
            }

            // Update password
            put("/me/password") {
                service.putMyPassword(call)
            }
        }
    }

    // Endpoint publik untuk akses foto (tanpa login)
    get("/users/{id}/photo") {
        service.getPhoto(call)
    }
}