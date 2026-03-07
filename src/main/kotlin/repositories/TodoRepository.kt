package org.delcom.repositories

import org.delcom.dao.TodoDAO
import org.delcom.entities.Todo
import org.delcom.helpers.suspendTransaction
import org.delcom.helpers.todoDAOToModel
import org.delcom.tables.TodoTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class TodoRepository : ITodoRepository {
    // 1. Update fungsi getAll dengan parameter Pagination & Filter
    override suspend fun getAll(
        userId: String,
        search: String,
        page: Int,
        perPage: Int,
        status: String?,
        urgency: String?
    ): List<Todo> = suspendTransaction {
        // Logic Pagination
        val offset = ((page - 1) * perPage).toLong()

        // Membangun Query
        val query = TodoDAO.find {
            val conditions = (TodoTable.userId eq UUID.fromString(userId))

            // Tambahkan filter pencarian (Lower Case)
            val searchCondition = if (search.isNotBlank()) {
                val keyword = "%${search.lowercase()}%"
                (TodoTable.title.lowerCase() like keyword) or (TodoTable.description.lowerCase() like keyword)
            } else null

            // Tambahkan filter status
            val statusCondition = when (status) {
                "done" -> (TodoTable.isDone eq true)
                "undone" -> (TodoTable.isDone eq false)
                else -> null
            }

            // Tambahkan filter urgency
            val urgencyCondition = if (!urgency.isNullOrEmpty()) {
                (TodoTable.urgency eq urgency)
            } else null

            // Gabungkan semua kondisi
            listOfNotNull(conditions, searchCondition, statusCondition, urgencyCondition)
                .reduce { acc, op -> acc and op }
        }

        // PERBAIKAN: Pisahkan limit dan offset untuk menghindari deprecation error
        // Pastikan orderBy diletakkan sebelum limit/offset
        query.orderBy(TodoTable.createdAt to SortOrder.DESC)
            .limit(perPage)
            .offset(offset)
            .map(::todoDAOToModel)
    }

    override suspend fun getById(todoId: String): Todo? = suspendTransaction {
        TodoDAO
            .find {
                (TodoTable.id eq UUID.fromString(todoId))
            }
            .limit(1)
            .map(::todoDAOToModel)
            .firstOrNull()
    }

    override suspend fun create(todo: Todo): String = suspendTransaction {
        val todoDAO = TodoDAO.new {
            userId = UUID.fromString(todo.userId)
            title = todo.title
            description = todo.description
            cover = todo.cover
            isDone = todo.isDone
            // Tetap simpan tingkat urgensi ke database
            urgency = todo.urgency
            createdAt = todo.createdAt
            updatedAt = todo.updatedAt
        }

        todoDAO.id.value.toString()
    }

    override suspend fun update(userId: String, todoId: String, newTodo: Todo): Boolean = suspendTransaction {
        val todoDAO = TodoDAO
            .find {
                (TodoTable.id eq UUID.fromString(todoId)) and
                        (TodoTable.userId eq UUID.fromString(userId))
            }
            .limit(1)
            .firstOrNull()

        if (todoDAO != null) {
            todoDAO.title = newTodo.title
            todoDAO.description = newTodo.description
            todoDAO.cover = newTodo.cover
            todoDAO.isDone = newTodo.isDone
            // Tetap perbarui tingkat urgensi
            todoDAO.urgency = newTodo.urgency
            todoDAO.updatedAt = newTodo.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun delete(userId: String, todoId: String): Boolean = suspendTransaction {
        val rowsDeleted = TodoTable.deleteWhere {
            (TodoTable.id eq UUID.fromString(todoId)) and
                    (TodoTable.userId eq UUID.fromString(userId))
        }
        rowsDeleted >= 1
    }
}