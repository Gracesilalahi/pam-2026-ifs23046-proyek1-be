-- 1. Aktifkan ekstensi UUID (agar Postgres bisa generate ID otomatis jika perlu)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Tabel Users (Pondasi utama)
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY,
                                     username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    about TEXT
    );

-- 3. Tabel Refresh Tokens (Untuk sesi login)
CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              id UUID PRIMARY KEY,
                                              token VARCHAR(500) NOT NULL,
    user_id UUID NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_token FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- 4. Tabel Wardrobes (Koleksi baju Digital Wardrobe kamu)
CREATE TABLE IF NOT EXISTS wardrobes (
                                         id UUID PRIMARY KEY,
                                         user_id UUID NOT NULL,
                                         name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL, -- Nanti isinya: TOP, BOTTOM, shoes, dll.
    color VARCHAR(50),
    image_path VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_wardrobe FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );