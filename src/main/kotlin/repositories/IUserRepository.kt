package org.delcom.repositories

import org.delcom.entities.User

interface IUserRepository {
    suspend fun getByUsername(username: String): User?
    suspend fun getById(id: String): User?
    suspend fun update(id: String, user: User): Boolean
    suspend fun create(user: User): String // Tambahkan ini
    suspend fun delete(id: String): Boolean // Tambahkan ini
}