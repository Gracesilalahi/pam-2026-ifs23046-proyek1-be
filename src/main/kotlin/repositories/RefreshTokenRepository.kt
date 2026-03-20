package org.delcom.repositories

import org.delcom.dao.RefreshTokenDAO
import org.delcom.entities.RefreshToken
import org.delcom.helpers.refreshTokenDAOToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.RefreshTokenTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import java.time.LocalDateTime
import java.util.*

class RefreshTokenRepository : IRefreshTokenRepository {

    override suspend fun insert(userId: String, refreshToken: RefreshToken): String = suspendTransaction {
        RefreshTokenDAO.new {
            this.token = refreshToken.refreshToken
            this.userId = UUID.fromString(userId)
            this.expiryDate = LocalDateTime.now().plusDays(7) // Contoh expiry 7 hari
        }.id.value.toString()
    }

    override suspend fun getByToken(token: String): RefreshToken? = suspendTransaction {
        RefreshTokenDAO.find { RefreshTokenTable.token eq token }
            .firstOrNull()?.let { refreshTokenDAOToModel(it) }
    }

    override suspend fun deleteByUserId(userId: String): Boolean = suspendTransaction {
        RefreshTokenTable.deleteWhere { RefreshTokenTable.userId eq UUID.fromString(userId) } > 0
    }
}