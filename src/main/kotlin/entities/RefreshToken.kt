package org.delcom.entities

import kotlinx.serialization.Serializable

@Serializable
data class RefreshToken(
    val id: String,
    val refreshToken: String, // Hanya simpan refresh token
    val userId: String,
    val expiryDate: String
)