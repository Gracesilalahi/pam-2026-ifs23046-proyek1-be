package org.delcom.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object WardrobeTable : Table("wardrobe") {
    val id = varchar("id", 50)
    val userId = varchar("user_id", 50)
    val name = varchar("name", 100)
    val category = enumerationByName("category", 20, WardrobeCategory::class)

    // Di sini kuncinya: Tambahkan .nullable()
    val color = text("color").nullable()
    val description = text("description").nullable()
    val imagePath = text("photo").nullable()

    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}