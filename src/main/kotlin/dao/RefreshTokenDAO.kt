package org.delcom.dao

import org.delcom.tables.RefreshTokenTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class RefreshTokenDAO(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RefreshTokenDAO>(RefreshTokenTable)

    var token by RefreshTokenTable.token
    var userId by RefreshTokenTable.userId
    var expiryDate by RefreshTokenTable.expiryDate
}