package org.delcom.data

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class BaseResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
)

@Serializable
data class PaginationData<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val hasNext: Boolean
)

@Serializable
data class ErrorResponse(
    val status: String = "error",
    val errorCode: String,
    val message: String,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
)