package org.delcom

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.delcom.routes.* // Meng-import SEMUA file di package org.delcom.routes
import org.delcom.services.*

fun Application.configureRouting(
    authService: AuthService,
    userService: UserService,
    wardrobeService: WardrobeService
) {
    routing {
        // Fungsi-fungsi di bawah ini harus ada di package org.delcom.routes
        authRoutes(authService)
        userRoutes(userService)
        wardrobeRoutes(wardrobeService)
    }
}