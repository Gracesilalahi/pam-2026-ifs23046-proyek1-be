package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.TodoRequest
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ITodoRepository
import org.delcom.repositories.IUserRepository
import java.io.File
import java.util.*

class TodoService(
    private val userRepo: IUserRepository,
    private val todoRepo: ITodoRepository
) {
    // Mengambil semua daftar todo saya dengan Pagination dan Filter
    suspend fun getAll(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)

        // Parameter pencarian yang sudah ada
        val search = call.request.queryParameters["search"] ?: ""

        // TAMBAHAN: Parameter Pagination sesuai instruksi (default 10 data)
        val page = call.request.queryParameters["page"]?.toInt() ?: 1
        val perPage = call.request.queryParameters["perPage"]?.toInt() ?: 10

        // TAMBAHAN: Parameter Filter Status dan Urgency
        val status = call.request.queryParameters["status"] // misal: "done" atau "undone"
        val urgency = call.request.queryParameters["urgency"] // misal: "Low", "Medium", "High"

        // Memanggil repo dengan parameter lengkap untuk optimasi resource
        val todos = todoRepo.getAll(user.id, search, page, perPage, status, urgency)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar todo saya",
            mapOf(Pair("todos", todos))
        )
        call.respond(response)
    }

    // Mengambil daftar todo saya dengan id
    suspend fun getById(call: ApplicationCall) {
        val todoId = call.parameters["id"]
            ?: throw AppException(400, "Data todo tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        val todo = todoRepo.getById(todoId)
        if (todo == null || todo.userId != user.id) {
            throw AppException(404, "Data todo tidak tersedia!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengambil data todo",
            mapOf(Pair("todo", todo))
        )
        call.respond(response)
    }

    // Ubah cover todo
    suspend fun putCover(call: ApplicationCall) {
        val todoId = call.parameters["id"]
            ?: throw AppException(400, "Data todo tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        // Ambil data request
        val request = TodoRequest()
        request.userId = user.id

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                // Upload file
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/todos/$fileName"

                    withContext(Dispatchers.IO) {
                        val file = File(filePath)
                        file.parentFile.mkdirs() // pastikan folder ada

                        part.provider().copyAndClose(file.writeChannel())
                        request.cover = filePath
                    }
                }

                else -> {}
            }

            part.dispose()
        }

        if (request.cover == null) {
            throw AppException(404, "Cover todo tidak tersedia!")
        }

        val newFile = File(request.cover!!)
        // Cek apakah gambar berhasil diunggah
        if (!newFile.exists()) {
            throw AppException(404, "Cover todo gagal diunggah!")
        }

        val oldTodo = todoRepo.getById(todoId)
        if (oldTodo == null || oldTodo.userId != user.id) {
            throw AppException(404, "Data todo tidak tersedia!")
        }

        request.title = oldTodo.title
        request.description = oldTodo.description
        request.isDone = oldTodo.isDone
        // TAMBAHAN: Pastikan nilai urgency tetap terjaga saat ganti cover
        request.urgency = oldTodo.urgency

        val isUpdated = todoRepo.update(
            user.id,
            todoId,
            request.toEntity()
        )
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui cover todo!")
        }

        // Hapus cover todo lama
        if (oldTodo.cover != null) {
            val oldFile = File(oldTodo.cover!!)
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah cover todo",
            null
        )
        call.respond(response)
    }

    // Menambahkan data todo dengan Urgency Level
    suspend fun post(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)

        // Ambil data request
        val request = call.receive<TodoRequest>()
        request.userId = user.id

        // Validasi request
        val validator = ValidatorHelper(request.toMap())
        validator.required("title", "Judul todo tidak boleh kosong")
        validator.required("description", "Deskripsi tidak boleh kosong")
        // TAMBAHAN: Validasi urgency sesuai ketentuan praktikum
        validator.required("urgency", "Tingkat urgensi tidak boleh kosong")
        validator.validate()

        // Tambahkan todo
        val todoId = todoRepo.create(
            request.toEntity()
        )

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data todo",
            mapOf(Pair("todoId", todoId))
        )
        call.respond(response)
    }

    // Mengubah data todo termasuk Urgency Level
    suspend fun put(call: ApplicationCall) {
        val todoId = call.parameters["id"]
            ?: throw AppException(400, "Data todo tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        // Ambil data request
        val request = call.receive<TodoRequest>()
        request.userId = user.id

        // Validasi request
        val validator = ValidatorHelper(request.toMap())
        validator.required("title", "Judul todo tidak boleh kosong")
        validator.required("description", "Deskripsi tidak boleh kosong")
        validator.required("isDone", "Status selesai tidak boleh kosong")
        // TAMBAHAN: Validasi urgency level
        validator.required("urgency", "Tingkat urgensi tidak boleh kosong")
        validator.validate()

        val oldTodo = todoRepo.getById(todoId)
        if (oldTodo == null || oldTodo.userId != user.id) {
            throw AppException(404, "Data todo tidak tersedia!")
        }
        request.cover = oldTodo.cover

        val isUpdated = todoRepo.update(
            user.id,
            todoId,
            request.toEntity()
        )
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data todo!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data todo",
            null
        )
        call.respond(response)
    }

    // Menghapus data todo
    suspend fun delete(call: ApplicationCall) {
        val todoId = call.parameters["id"]
            ?: throw AppException(400, "Data todo tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        val oldTodo = todoRepo.getById(todoId)
        if (oldTodo == null || oldTodo.userId != user.id) {
            throw AppException(404, "Data todo tidak tersedia!")
        }

        val isDeleted = todoRepo.delete(user.id, todoId)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data todo!")
        }

        if (oldTodo.cover != null) {
            val oldFile = File(oldTodo.cover!!)

            // Hapus data gambar jika data todo sudah dihapus
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val response = DataResponse(
            "success",
            "Berhasil menghapus data todo",
            null
        )
        call.respond(response)
    }

    // Mengambil gambar todo
    suspend fun getCover(call: ApplicationCall) {
        val todoId = call.parameters["id"]
            ?: throw AppException(400, "Data todo tidak valid!")

        val todo = todoRepo.getById(todoId)
            ?: return call.respond(HttpStatusCode.NotFound)

        if (todo.cover == null) {
            throw AppException(404, "Todo belum memiliki cover")
        }

        val file = File(todo.cover!!)
        if (!file.exists()) {
            throw AppException(404, "Cover todo tidak tersedia")
        }

        call.respondFile(file)
    }
}