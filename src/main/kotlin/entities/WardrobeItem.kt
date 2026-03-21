package org.delcom.entities
import kotlinx.serialization.Serializable

@Serializable
data class WardrobeItem(
    val id: String,
    val name: String,
    val category: WardrobeCategory,
    val color: String? = null,
    val imagePath: String? = null,
    val description: String? = null,
    val createdAt: String? = null
)