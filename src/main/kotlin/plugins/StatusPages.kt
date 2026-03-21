package org.delcom.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.delcom.data.ErrorResponse
import java.time.LocalDateTime

fun Application.configureStatusPages() {
    install(StatusPages) {

        // 1. Tangkap Status 401 (Unauthorized) secara global
        // Cara ini lebih aman daripada menangkap Exception-nya
        status(HttpStatusCode.Unauthorized) { call, status ->
            call.respond(
                status,
                ErrorResponse(
                    errorCode = "401",
                    message = "Token tidak valid atau sesi berakhir",
                    timestamp = LocalDateTime.now().toString()
                )
            )
        }

        // 2. Tangkap Status 404 (Not Found)
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status,
                ErrorResponse(
                    errorCode = "404",
                    message = "Endpoint tidak ditemukan",
                    timestamp = LocalDateTime.now().toString()
                )
            )
        }

        // 3. Handle IllegalArgumentException (Error 400)
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    errorCode = "400",
                    message = cause.message ?: "Request tidak valid",
                    timestamp = LocalDateTime.now().toString()
                )
            )
        }

        // 4. Handle Global Exception (Error 500)
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    errorCode = "500",
                    message = "Terjadi kesalahan sistem: ${cause.message}",
                    timestamp = LocalDateTime.now().toString()
                )
            )
        }
    }
}