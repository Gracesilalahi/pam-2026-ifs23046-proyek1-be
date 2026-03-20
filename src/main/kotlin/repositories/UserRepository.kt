package org.delcom.repositories

import org.delcom.dao.UserDAO
import org.delcom.entities.User
import org.delcom.helpers.suspendTransaction
import org.delcom.helpers.userDAOToModel
import org.delcom.tables.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID

class UserRepository : IUserRepository {
    override suspend fun getByUsername(username: String): User? = suspendTransaction {
        UserDAO.find { UserTable.username eq username }.firstOrNull()?.let { userDAOToModel(it) }
    }

    override suspend fun getById(id: String): User? = suspendTransaction {
        try {
            UserDAO.findById(UUID.fromString(id))?.let { userDAOToModel(it) }
        } catch (e: Exception) { null }
    }

    override suspend fun create(user: User): String = suspendTransaction {
        UserTable.insert {
            it[id] = UUID.fromString(user.id)
            it[username] = user.username
            it[password] = user.password
            it[name] = user.name
            it[about] = user.about
        }
        user.id
    }

    override suspend fun update(id: String, user: User): Boolean = suspendTransaction {
        val affectedRows = UserTable.update({ UserTable.id eq UUID.fromString(id) }) {
            it[name] = user.name
            it[username] = user.username
            it[password] = user.password
            it[about] = user.about
        }
        affectedRows > 0
    }

    override suspend fun delete(id: String): Boolean = suspendTransaction {
        UserTable.deleteWhere { UserTable.id eq UUID.fromString(id) } > 0
    }
}