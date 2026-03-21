package org.delcom.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.WardrobeRequest
import org.delcom.services.WardrobeService
import org.delcom.helpers.SecurityHelper
import org.delcom.helpers.JWTConstants

fun Route.wardrobeRoutes(service: WardrobeService) {
    authenticate(JWTConstants.NAME) {
        route("/wardrobe") {

            // 1. Tambah Baju (Versi JSON)
            post {
                try {
                    val userId = SecurityHelper.extractUserId(call)
                        ?: return@post call.respond(HttpStatusCode.Unauthorized)

                    // Membaca body sebagai JSON WardrobeRequest
                    val request = call.receive<WardrobeRequest>()

                    val response = service.addWardrobeItem(userId, request, null)
                    call.respond(response)
                } catch (e: Exception) {
                    // Jika JSON tidak cocok, lari ke sini
                    throw IllegalArgumentException("Format JSON baju tidak valid: ${e.message}")
                }
            }

            // 2. Daftar Baju (Paged & Filter)
            get {
                val userId = SecurityHelper.extractUserId(call) ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val page = call.parameters["page"]?.toIntOrNull() ?: 1
                val perPage = call.parameters["perPage"]?.toIntOrNull() ?: 10
                val search = call.parameters["search"]
                val category = call.parameters["category"]
                val sortBy = call.parameters["sortBy"] ?: "createdAt"
                val order = call.parameters["order"] ?: "desc"

                call.respond(service.getPagedWardrobe(userId, page, perPage, search, category, sortBy, order))
            }

            // 3. Detail Baju
            get("/{id}") {
                val userId = SecurityHelper.extractUserId(call) ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"] ?: throw IllegalArgumentException("ID wajib diisi")
                call.respond(service.getDetail(id, userId))
            }

            // 4. Update Baju
            put("/{id}") {
                val userId = SecurityHelper.extractUserId(call) ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"] ?: throw IllegalArgumentException("ID wajib diisi")
                val request = call.receive<WardrobeRequest>()

                call.respond(service.updateItem(id, userId, request, null))
            }

            // 5. Hapus Baju
            delete("/{id}") {
                val userId = SecurityHelper.extractUserId(call) ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"] ?: throw IllegalArgumentException("ID wajib diisi")
                call.respond(service.deleteItem(id, userId))
            }
        }
    }
}