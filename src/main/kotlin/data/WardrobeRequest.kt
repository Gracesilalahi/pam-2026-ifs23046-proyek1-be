package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class WardrobeRequest(
    val name: String,
    val category: String,
    val color: String,
    val description: String? = null
)