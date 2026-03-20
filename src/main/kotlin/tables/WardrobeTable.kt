package org.delcom.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object WardrobeTable : Table("wardrobe_items") {
    val id = varchar("id", 50)
    val userId = varchar("user_id", 50)
    val name = varchar("name", 255)
    val category = varchar("category", 50)
    val color = varchar("color", 50)
    val imagePath = varchar("image_path", 500).nullable()
    val description = text("description").nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}