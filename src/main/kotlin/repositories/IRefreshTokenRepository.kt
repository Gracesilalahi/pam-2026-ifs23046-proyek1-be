package org.delcom.repositories

import org.delcom.entities.RefreshToken

interface IRefreshTokenRepository {
    suspend fun insert(userId: String, refreshToken: RefreshToken): String
    suspend fun getByToken(token: String): RefreshToken?
    suspend fun deleteByUserId(userId: String): Boolean
}