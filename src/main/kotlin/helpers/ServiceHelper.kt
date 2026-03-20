package org.delcom.helpers

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import kotlinx.coroutines.Dispatchers
import org.delcom.data.AppException
import org.delcom.entities.User
import org.delcom.repositories.IUserRepository
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

// Fungsi transaksi database (Wajib top-level agar bisa di-import)
suspend fun <T> suspendTransaction(block: suspend Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

object ServiceHelper {
    suspend fun getAuthUser(call: ApplicationCall, userRepo: IUserRepository): User {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim("userId")?.asString()
            ?: throw AppException(401, "Sesi berakhir, login kembali.")

        return userRepo.getById(userId) ?: throw AppException(404, "User tidak ditemukan.")
    }
}