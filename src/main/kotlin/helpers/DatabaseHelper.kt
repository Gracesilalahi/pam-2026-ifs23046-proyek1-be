package org.delcom.helpers

import io.ktor.server.application.*
import org.delcom.tables.RefreshTokenTable // Sesuaikan package-nya
import org.delcom.tables.TodoTable         // Sesuaikan package-nya
import org.delcom.tables.UserTable         // Sesuaikan package-nya
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val dbHost = environment.config.property("ktor.database.host").getString()
    val dbPort = environment.config.property("ktor.database.port").getString()
    val dbName = environment.config.property("ktor.database.name").getString()
    val dbUser = environment.config.property("ktor.database.user").getString()
    val dbPassword = environment.config.property("ktor.database.password").getString()

    Database.connect(
        url = "jdbc:postgresql://$dbHost:$dbPort/$dbName",
        user = dbUser,
        password = dbPassword,
        driver = "org.postgresql.Driver"
    )

    // TAMBAHKAN BLOK INI
    transaction {
        // Ini yang membuat aplikasi "sadar" kalau tabel-tabel ini ada
        SchemaUtils.create(UserTable, TodoTable, RefreshTokenTable)
    }
}