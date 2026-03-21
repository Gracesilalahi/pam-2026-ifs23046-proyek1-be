package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val errorCode: String,
    val message: String,
    val timestamp: String
)