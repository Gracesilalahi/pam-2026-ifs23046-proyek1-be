package org.delcom.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.delcom.data.ErrorResponse

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.application.log.error("Validation Error: ${cause.message}")
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(errorCode = "400", message = cause.message ?: "Input tidak valid"))
        }
        exception<IllegalStateException> { call, cause ->
            call.application.log.error("Access Denied: ${cause.message}")
            call.respond(HttpStatusCode.Forbidden, ErrorResponse(errorCode = "403", message = cause.message ?: "Akses ditolak"))
        }
        exception<Throwable> { call, cause ->
            call.application.log.error("Server Error: ${cause.localizedMessage}")
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(errorCode = "500", message = "Terjadi kesalahan sistem"))
        }
    }
}