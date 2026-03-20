package org.delcom.repositories

import org.delcom.entities.WardrobeItem

interface IWardrobeRepository {
    suspend fun insert(userId: String, item: WardrobeItem): Int
    suspend fun findPaged(userId: String, limit: Int, offset: Long, search: String?, category: String?, sortBy: String, order: String): List<WardrobeItem>
    suspend fun findByIdAndUserId(id: String, userId: String): WardrobeItem?
    suspend fun countData(userId: String, search: String?, category: String?): Long
    suspend fun update(id: String, userId: String, item: WardrobeItem): Int
    suspend fun delete(id: String, userId: String): Int
}