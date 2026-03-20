package org.delcom.entities

import kotlinx.serialization.Serializable

@Serializable
enum class WardrobeCategory {
    TOPS, BOTTOMS, SHOES, ACCESSORIES
}

@Serializable
data class WardrobeItem(
    val id: String,
    val name: String,
    val category: WardrobeCategory,
    val color: String,
    val imagePath: String?,
    val description: String?,
    val createdAt: String
)