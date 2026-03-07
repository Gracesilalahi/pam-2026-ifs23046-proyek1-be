package org.delcom.repositories

import org.delcom.entities.Todo

interface ITodoRepository {
    // Diperbarui agar mendukung Pagination, Filter Status, dan Urgency
    suspend fun getAll(
        userId: String,
        search: String,
        page: Int,           // Tambahan untuk Pagination
        perPage: Int,        // Tambahan untuk Pagination
        status: String?,     // Tambahan untuk Filter Status
        urgency: String?     // Tambahan untuk Filter Urgency
    ): List<Todo>

    suspend fun getById(todoId: String): Todo?

    suspend fun create(todo: Todo): String

    suspend fun update(userId: String, todoId: String, newTodo: Todo): Boolean

    suspend fun delete(userId: String, todoId: String): Boolean
}