package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import org.delcom.data.*
import org.delcom.entities.User
import org.delcom.entities.RefreshToken
import org.delcom.repositories.IUserRepository
import org.delcom.repositories.IRefreshTokenRepository
import org.delcom.helpers.hashPassword
import org.delcom.helpers.verifyPassword
import java.util.*

class AuthService(
    private val jwtSecret: String,
    private val userRepo: IUserRepository,
    private val refreshTokenRepo: IRefreshTokenRepository
) {
    suspend fun register(call: ApplicationCall) {
        val request = call.receive<AuthRequest>()
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
        val user = userRepo.getByUsername(request.username)
            ?: throw IllegalArgumentException("Username tidak ditemukan")

        if (!verifyPassword(request.password, user.password)) {
            throw IllegalArgumentException("Password salah")
        }

        // Simulasikan token (Nanti hubungkan dengan JWT generator-mu)
        val token = "dummy-jwt-token"
        val refresh = UUID.randomUUID().toString()

        // PERBAIKAN: Jangan masukkan authToken ke sini
        val refreshTokenEntity = RefreshToken(
            id = UUID.randomUUID().toString(),
            refreshToken = refresh,
            userId = user.id,
            expiryDate = "" // Akan diisi di Repository
        )

        refreshTokenRepo.insert(user.id, refreshTokenEntity)

        call.respond(BaseResponse("success", "Login berhasil", mapOf(
            "token" to token,
            "refreshToken" to refresh
        )))
    }

    suspend fun refreshToken(call: ApplicationCall) {
        call.respond(BaseResponse("success", "Token refreshed", null))
    }
}