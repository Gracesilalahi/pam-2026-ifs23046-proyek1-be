package org.delcom.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.WardrobeRequest
import org.delcom.services.WardrobeService
import org.delcom.helpers.FileHelper
import org.delcom.helpers.SecurityHelper
import org.delcom.helpers.JWTConstants

fun Route.wardrobeRoutes(service: WardrobeService) {
    authenticate(JWTConstants.NAME) {
        route("/wardrobe") {

            // 1. Tambah Baju (Multipart/Upload)
            post {
                val userId = SecurityHelper.extractUserId(call) ?: return@post call.respond(HttpStatusCode.Unauthorized)

                var name = ""; var category = ""; var color = ""; var description: String? = null; var fileName: String? = null
                val multipart = call.receiveMultipart()

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "name" -> name = part.value
                                "category" -> category = part.value
                                "color" -> color = part.value
                                "description" -> description = part.value
                            }
                        }
                        is PartData.FileItem -> {
                            fileName = FileHelper.validateAndSaveFile(part)
                        }
                        else -> part.dispose()
                    }
                }

                val response = service.addWardrobeItem(userId, WardrobeRequest(name, category, color, description), fileName)
                call.respond(response)
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

            // 4. Hapus Baju
            delete("/{id}") {
                val userId = SecurityHelper.extractUserId(call) ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"] ?: throw IllegalArgumentException("ID wajib diisi")
                call.respond(service.deleteItem(id, userId))
            }
        }
    }
}