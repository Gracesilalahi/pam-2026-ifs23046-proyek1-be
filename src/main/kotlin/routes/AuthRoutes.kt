package org.delcom.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.delcom.services.AuthService

fun Route.authRoutes(service: AuthService) {
    route("/auth") {
        post("/register") {
            service.register(call)
        }
        post("/login") {
            service.login(call)
        }
        post("/refresh-token") {
            service.refreshToken(call)
        }
    }
}