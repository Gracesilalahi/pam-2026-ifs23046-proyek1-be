package org.delcom.services

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.delcom.data.AppException
import org.delcom.data.AuthRequest
import org.delcom.data.BaseResponse
import org.delcom.data.UserResponse
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.helpers.hashPassword
import org.delcom.helpers.verifyPassword
import org.delcom.repositories.IRefreshTokenRepository
import org.delcom.repositories.IUserRepository
import java.io.File as JavaFile // Gunakan alias untuk menghindari konflik
import java.util.*

class UserService(
    private val userRepo: IUserRepository,
    private val refreshTokenRepo: IRefreshTokenRepository,
) {
    suspend fun getMe(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        val response = BaseResponse(
            "success",
            "Berhasil mengambil informasi akun saya",
            mapOf(
                "user" to UserResponse(
                    id = user.id,
                    name = user.name,
                    username = user.username,
                    photo = user.photo,
                    about = user.about,
                    createdAt = user.createdAt,
                    updatedAt = user.updatedAt,
                )
            )
        )
        call.respond(response)
    }

    suspend fun putMe(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<AuthRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("name", "Nama tidak boleh kosong")
        validator.required("username", "Username tidak boleh kosong")
        validator.validate()

        val existUser = userRepo.getByUsername(request.username)
        if (existUser != null && existUser.username != user.username) {
            throw AppException(409, "Akun dengan username ini sudah terdaftar!")
        }

        user.username = request.username
        user.name = request.name
        user.about = request.about

        val isUpdated = userRepo.update(user.id, user)
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data profile!")

        call.respond(BaseResponse("success", "Berhasil mengubah data profile", null))
    }

    suspend fun putMyPhoto(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        var newPhoto: String? = null
        val multipartData = call.receiveMultipart()

        multipartData.forEachPart { part ->
            if (part is PartData.FileItem) {
                val ext = part.originalFileName?.substringAfterLast('.', "")?.let { ".$it" } ?: ""
                val fileName = UUID.randomUUID().toString() + ext
                val filePath = "uploads/users/$fileName"

                withContext(Dispatchers.IO) {
                    val file = JavaFile(filePath)
                    file.parentFile.mkdirs()
                    part.provider().copyAndClose(file.writeChannel())
                    newPhoto = filePath
                }
            }
            part.dispose()
        }

        if (newPhoto == null) throw AppException(404, "Photo profile tidak tersedia!")

        val oldPhoto = user.photo
        user.photo = newPhoto

        if (!userRepo.update(user.id, user)) throw AppException(400, "Gagal update foto!")

        if (oldPhoto != null) {
            val oldFile = JavaFile(oldPhoto)
            if (oldFile.exists()) oldFile.delete()
        }

        call.respond(BaseResponse("success", "Berhasil mengubah photo profile", null))
    }

    suspend fun putMyPassword(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<AuthRequest>()

        if (!verifyPassword(request.password, user.password)) {
            throw AppException(401, "Kata sandi lama tidak valid!")
        }

        user.password = hashPassword(request.newPassword)
        if (!userRepo.update(user.id, user)) throw AppException(400, "Gagal ubah sandi!")

        refreshTokenRepo.deleteByUserId(user.id)
        call.respond(BaseResponse("success", "Berhasil mengubah kata sandi", null))
    }

    suspend fun getPhoto(call: ApplicationCall) {
        val userId = call.parameters["id"] ?: throw AppException(400, "ID tidak valid!")
        val user = userRepo.getById(userId) ?: throw AppException(404, "User tidak ditemukan!")

        val photoPath = user.photo ?: throw AppException(404, "User belum memiliki photo")
        val file = JavaFile(photoPath)

        if (!file.exists()) throw AppException(404, "Photo tidak tersedia di server")
        call.respondFile(file)
    }
}