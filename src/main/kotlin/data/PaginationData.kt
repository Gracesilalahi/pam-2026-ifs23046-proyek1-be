package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class PaginationData<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val hasNext: Boolean
)