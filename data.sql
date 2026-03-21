-- 1. Bersihkan tabel lama (Hati-hati: ini akan menghapus data yang ada)
DROP TABLE IF EXISTS wardrobe, refresh_tokens, users CASCADE;

-- 2. Tabel Users (ID pakai VARCHAR agar cocok dengan Kotlin kamu)
CREATE TABLE users (
                       id VARCHAR(50) PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       about TEXT
);

-- 3. Tabel Refresh Tokens (Untuk sesi login)
CREATE TABLE refresh_tokens (
                                id VARCHAR(50) PRIMARY KEY,
                                token VARCHAR(500) NOT NULL,
                                user_id VARCHAR(50) NOT NULL,
                                expiry_date TIMESTAMP NOT NULL,
                                CONSTRAINT fk_user_token FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Tabel Wardrobe (Pusat data baju kamu)
CREATE TABLE wardrobe (
                          id VARCHAR(50) PRIMARY KEY,
                          user_id VARCHAR(50) NOT NULL,
                          name VARCHAR(100) NOT NULL,
                          category VARCHAR(20) NOT NULL, -- Isinya: TOP, BOTTOM, dll.
                          color TEXT,
                          photo TEXT, -- Sesuai dengan text("photo") di Kotlin kamu
                          description TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_user_wardrobe FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);