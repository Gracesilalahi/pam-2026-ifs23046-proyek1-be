package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object TodoTable : UUIDTable("todos") {
    // Tambahkan references agar relasi ke UserTable terjamin
    val userId = uuid("user_id").references(UserTable.id)
    val title = varchar("title", 100)
    val description = text("description")
    val cover = text("cover").nullable()

    // Tambahkan default(false) agar data awal selalu belum selesai
    val isDone = bool("is_done").default(false)

    // TAMBAHKAN INI: Sesuai instruksi fitur level urgensi
    val urgency = varchar("urgency", 10).default("Low")

    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}