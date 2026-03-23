package org.delcom

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.* // PENTING: Untuk fungsi respondText
import org.delcom.routes.*
import org.delcom.services.*

fun Application.configureRouting(
    authService: AuthService,
    userService: UserService,
    wardrobeService: WardrobeService
) {
    routing {

        // 1. Home Route: Biar di browser muncul tulisan (Gak 404 lagi)
        get("/") {
            call.respondText("🚀 Backend Wardrobe Grace Project 1 is Running!")
        }

        // 2. Auth Routes: Untuk Register & Login
        // Pastikan file authRoutes.kt kamu di folder routes sudah benar
        authRoutes(authService)

        // 3. User Routes: Untuk Profile, dll
        userRoutes(userService)

        // 4. Wardrobe Routes: Untuk Kelola Baju
        wardrobeRoutes(wardrobeService)

    }
}