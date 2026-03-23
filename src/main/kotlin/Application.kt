package org.delcom

import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import org.delcom.helpers.JWTConstants
import org.delcom.helpers.configureDatabases
import org.delcom.module.appModule
import org.delcom.plugins.configureStatusPages
import org.delcom.services.AuthService
import org.delcom.services.UserService
import org.delcom.services.WardrobeService
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

fun main(args: Array<String>) {
    // 1. Perbaikan dotenv: ignoreIfMissing diset TRUE biar gak crash di server
    val dotenv = dotenv {
        directory = "."
        ignoreIfMissing = true
    }
    dotenv.entries().forEach { System.setProperty(it.key, it.value) }

    EngineMain.main(args)
}

fun Application.module() {
    // 2. Ambil secret dari application.yaml, kalau gak ada baru pakai default
    val jwtSecret = environment.config.propertyOrNull("ktor.jwt.secret")?.getString() ?: "rahasia_grace_123_abc"

    install(Koin) {
        modules(appModule(jwtSecret))
    }

    val authService by inject<AuthService>()
    val userService by inject<UserService>()
    val wardrobeService by inject<WardrobeService>()

    configureStatusPages()

    install(Authentication) {
        jwt(JWTConstants.NAME) {
            realm = JWTConstants.REALM
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(JWTConstants.ISSUER)
                    .withAudience(JWTConstants.AUDIENCE)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                if (!userId.isNullOrBlank()) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Token tidak valid"))
            }
        }
    }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }

    install(ContentNegotiation) {
        json(Json { explicitNulls = false; prettyPrint = true; ignoreUnknownKeys = true })
    }

    // 3. Pastikan urutan ini: DB dulu baru Routing
    configureDatabases()
    configureRouting(authService, userService, wardrobeService)
}