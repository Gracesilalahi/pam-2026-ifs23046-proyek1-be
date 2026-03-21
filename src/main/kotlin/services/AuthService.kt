package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.delcom.data.*
import org.delcom.entities.User
import org.delcom.entities.RefreshToken
import org.delcom.repositories.IUserRepository
import org.delcom.repositories.IRefreshTokenRepository
import org.delcom.helpers.hashPassword
import org.delcom.helpers.verifyPassword
import org.delcom.helpers.JWTConstants // PENTING: Pakai konstanta yang sama dengan Application.kt
import java.util.*

class AuthService(
    private val jwtSecret: String,
    private val userRepo: IUserRepository,
    private val refreshTokenRepo: IRefreshTokenRepository
) {
    suspend fun register(call: ApplicationCall) {
        val request = call.receive<AuthRequest>()

        // Cek apakah username sudah ada (biar nggak kena 500 duplicate key)
        val existingUser = userRepo.getByUsername(request.username)
        if (existingUser != null) {
            call.respond(HttpStatusCode.BadRequest, BaseResponse("error", "Username sudah digunakan", null))
            return
        }

        val newUser = User(
            id = UUID.randomUUID().toString(),
            username = request.username,
            password = hashPassword(request.password),
            name = request.name ?: request.username,
            about = ""
        )
        userRepo.create(newUser)
        call.respond(BaseResponse("success", "Registrasi berhasil", null))
    }

    suspend fun login(call: ApplicationCall) {
        val request = call.receive<AuthRequest>()

        // 1. Ambil user dari database
        val user = userRepo.getByUsername(request.username)
            ?: return call.respond(HttpStatusCode.Unauthorized, BaseResponse("error", "Username tidak ditemukan", null))

        // 2. Cek Password
        if (!verifyPassword(request.password, user.password)) {
            return call.respond(HttpStatusCode.Unauthorized, BaseResponse("error", "Password salah", null))
        }

        // 3. GENERATE TOKEN ASLI (Sinkron dengan Application.kt)
        val token = JWT.create()
            .withAudience(JWTConstants.AUDIENCE) // Pakai konstanta
            .withIssuer(JWTConstants.ISSUER)     // Pakai konstanta
            .withClaim("userId", user.id)        // Sesuai pengecekan di validate { ... }
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24 jam
            .sign(Algorithm.HMAC256(jwtSecret))

        // 4. Generate Refresh Token
        val refresh = UUID.randomUUID().toString()
        val refreshTokenEntity = RefreshToken(
            id = UUID.randomUUID().toString(),
            refreshToken = refresh,
            userId = user.id,
            expiryDate = ""
        )

        refreshTokenRepo.insert(user.id, refreshTokenEntity)

        // 5. Respond Sukses
        call.respond(BaseResponse("success", "Login berhasil", mapOf(
            "token" to token,
            "refreshToken" to refresh
        )))
    }

    suspend fun refreshToken(call: ApplicationCall) {
        // Logika refresh token bisa kamu kembangkan nanti
        call.respond(BaseResponse("success", "Token refreshed", null))
    }
}