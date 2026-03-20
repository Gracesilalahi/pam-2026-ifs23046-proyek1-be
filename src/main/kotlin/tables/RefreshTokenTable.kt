package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

object RefreshTokenTable : UUIDTable("refresh_tokens") {
    val token = varchar("token", 500)
    val userId = uuid("user_id")
    val expiryDate = datetime("expiry_date")
    // Note: Kita tidak pakai createdAt di sini agar sinkron dengan repository
}