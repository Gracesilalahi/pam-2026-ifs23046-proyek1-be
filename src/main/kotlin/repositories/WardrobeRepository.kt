@file:Suppress("DEPRECATION")

package org.delcom.repositories

import org.delcom.entities.WardrobeTable
import org.delcom.entities.WardrobeItem
import org.delcom.entities.WardrobeCategory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.delcom.helpers.suspendTransaction

class WardrobeRepository : IWardrobeRepository {

    override suspend fun insert(userId: String, item: WardrobeItem): Int = suspendTransaction {
        WardrobeTable.insert {
            it[id] = item.id
            it[WardrobeTable.userId] = userId
            it[name] = item.name
            it[category] = item.category
            it[color] = item.color ?: ""
            it[imagePath] = item.imagePath ?: ""
            it[description] = item.description ?: ""
        }
        1
    }

    override suspend fun findPaged(userId: String, limit: Int, offset: Long, search: String?, category: String?, sortBy: String, order: String): List<WardrobeItem> = suspendTransaction {
        var query = WardrobeTable.selectAll().where { WardrobeTable.userId eq userId }

        if (!search.isNullOrBlank()) {
            val searchTerm = "%${search.lowercase()}%"
            query = query.andWhere { WardrobeTable.name.lowerCase() like searchTerm }
        }

        if (!category.isNullOrBlank()) {
            try {
                val catEnum = WardrobeCategory.valueOf(category.uppercase())
                query = query.andWhere { WardrobeTable.category eq catEnum }
            } catch (e: Exception) { }
        }

        val sortCol = if (sortBy == "name") WardrobeTable.name else WardrobeTable.createdAt
        val sortOrd = if (order.lowercase() == "asc") SortOrder.ASC else SortOrder.DESC

        // --- FIX DEPRECATION DISINI ---
        query.orderBy(sortCol to sortOrd)
            .limit(limit)
            .offset(offset)
            .map { toDomain(it) }
    }

    override suspend fun findByIdAndUserId(id: String, userId: String): WardrobeItem? = suspendTransaction {
        WardrobeTable.selectAll()
            .where { (WardrobeTable.id eq id) and (WardrobeTable.userId eq userId) }
            .map { toDomain(it) }.singleOrNull()
    }

    override suspend fun countData(userId: String, search: String?, category: String?): Long = suspendTransaction {
        var query = WardrobeTable.selectAll().where { WardrobeTable.userId eq userId }
        if (!search.isNullOrBlank()) {
            val searchTerm = "%${search.lowercase()}%"
            query = query.andWhere { WardrobeTable.name.lowerCase() like searchTerm }
        }
        if (!category.isNullOrBlank()) {
            try {
                val catEnum = WardrobeCategory.valueOf(category.uppercase())
                query = query.andWhere { WardrobeTable.category eq catEnum }
            } catch (e: Exception) { }
        }
        query.count()
    }

    override suspend fun update(id: String, userId: String, item: WardrobeItem): Int = suspendTransaction {
        WardrobeTable.update({ (WardrobeTable.id eq id) and (WardrobeTable.userId eq userId) }) {
            it[name] = item.name
            it[category] = item.category
            it[color] = item.color ?: ""
            it[description] = item.description ?: ""
            if (item.imagePath != null) it[imagePath] = item.imagePath
        }
    }

    override suspend fun delete(id: String, userId: String): Int = suspendTransaction {
        WardrobeTable.deleteWhere { (WardrobeTable.id eq id) and (WardrobeTable.userId eq userId) }
    }

    private fun toDomain(row: ResultRow) = WardrobeItem(
        id = row[WardrobeTable.id],
        name = row[WardrobeTable.name],
        category = row[WardrobeTable.category],
        color = row[WardrobeTable.color],
        imagePath = row[WardrobeTable.imagePath],
        description = row[WardrobeTable.description],
        createdAt = row[WardrobeTable.createdAt].toString()
    )
}