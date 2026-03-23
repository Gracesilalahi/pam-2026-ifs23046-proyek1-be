package org.delcom.helpers

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

// --- KITA UBAH KE TABLES ---
import org.delcom.tables.UserTable
import org.delcom.entities.WardrobeTable
import org.delcom.tables.RefreshTokenTable

fun Application.configureDatabases() {
    // Mengambil data dari System.getProperty yang sudah diisi oleh dotenv di Application.kt
    val dbHost = System.getProperty("DB_HOST") ?: "127.0.0.1"
    val dbPort = System.getProperty("DB_PORT") ?: "5432"
    val dbName = System.getProperty("DB_NAME") ?: "db_pam_proyek1"
    val dbUser = System.getProperty("DB_USER") ?: "postgres"
    val dbPassword = System.getProperty("DB_PASSWORD") ?: "postgres"

    val url = "jdbc:postgresql://$dbHost:$dbPort/$dbName"

    // 1. Inisialisasi Koneksi ke PostgreSQL
    val db = Database.connect(
        url = url,
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPassword
    )

    // 2. Membuat Tabel Otomatis (Jika belum ada)
    // Bagian ini yang akan membereskan error "relation users does not exist"
    transaction(db) {
        // Menampilkan log query SQL di console (opsional, buat debug)
        // addLogger(StdOutSqlLogger)

        SchemaUtils.create(
            UserTable,
            WardrobeTable,
            RefreshTokenTable
        )
    }

    println("----------------------------------------------")
    println("✅ DATABASE STATUS: CONNECTED")
    println("🌍 URL: $url")
    println("🏠 TABLES: users, wardrobe, refresh_tokens")
    println("----------------------------------------------")
}