package org.delcom.tables

import kotlinx.datetime.Clock // Wajib di-import
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object UserTable : UUIDTable("users") {
    val name = varchar("name", 100)
    val username = varchar("username", 50)
    val password = varchar("password", 255)
    val photo = varchar("photo", 255).nullable()
    val about = text("about").nullable()

    // Tambahkan .clientDefault { Clock.System.now() }
    val createdAt = timestamp("created_at").clientDefault { Clock.System.now() }
    val updatedAt = timestamp("updated_at").clientDefault { Clock.System.now() }
}