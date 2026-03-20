package org.delcom.helpers

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

object SecurityHelper {
    fun extractUserId(call: ApplicationCall): String? {
        val principal = call.principal<JWTPrincipal>()
        return principal?.payload?.getClaim("userId")?.asString()
    }
}