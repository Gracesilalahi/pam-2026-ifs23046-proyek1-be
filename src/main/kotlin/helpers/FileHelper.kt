package org.delcom.helpers

import io.ktor.http.content.*
import java.io.File
import java.util.*

object FileHelper {
    private const val MAX_FILE_SIZE = 2 * 1024 * 1024
    private val ALLOWED_EXTENSIONS = listOf("jpg", "jpeg", "png")
    private const val UPLOAD_DIR = "uploads/wardrobe"

    fun validateAndSaveFile(part: PartData.FileItem): String {
        val ext = File(part.originalFileName ?: "img.png").extension.lowercase()

        if (ext !in ALLOWED_EXTENSIONS) throw IllegalArgumentException("Format tidak didukung (Gunakan JPG/PNG)")

        val directory = File(UPLOAD_DIR)
        if (!directory.exists()) directory.mkdirs()

        val fileName = "wardrobe_${UUID.randomUUID()}.$ext"
        val fileBytes = part.streamProvider().readBytes()

        if (fileBytes.size > MAX_FILE_SIZE) throw IllegalArgumentException("File terlalu besar (Maksimal 2MB)")

        File("$UPLOAD_DIR/$fileName").writeBytes(fileBytes)
        return fileName
    }
}