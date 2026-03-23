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
    val dotenv = dotenv {
        directory = "."
        ignoreIfMissing = true
    }

    dotenv.entries().forEach { entry ->
        // JANGAN ambil PORT dari .env kalau di server sudah ada variabel PORT
        if (entry.key != "PORT") {
            if (System.getenv(entry.key) == null && System.getProperty(entry.key) == null) {
                System.setProperty(entry.key, entry.value)
            }
        }
    }

    EngineMain.main(args)
}

fun Application.module() {
    // Ambil JWT Secret dari application.yaml (fall-back ke hardcoded jika tidak ada)
    val jwtSecret = environment.config.propertyOrNull("ktor.jwt.secret")?.getString() ?: "rahasia_grace_123_abc"

    // 1. PASANG KOIN TERLEBIH DAHULU
    install(Koin) {
        modules(appModule(jwtSecret))
    }

    // 2. BARU AMBIL SERVICE (Inject)
    val authService by inject<AuthService>()
    val userService by inject<UserService>()
    val wardrobeService by inject<WardrobeService>()

    configureStatusPages()

    // 3. KONFIGURASI AUTHENTICATION JWT
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
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("status" to "error", "message" to "Token tidak valid")
                )
            }
        }
    }

    // 4. KONFIGURASI CORS (Penting untuk Android/Web)
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }

    // 5. KONFIGURASI JSON SERIALIZATION
    install(ContentNegotiation) {
        json(Json {
            explicitNulls = false
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }

    // 6. INISIALISASI DATABASE & ROUTING
    configureDatabases() // Database status: CONNECTED akan muncul di sini
    configureRouting(authService, userService, wardrobeService)
}