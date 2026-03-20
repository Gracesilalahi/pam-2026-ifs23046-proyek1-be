package org.delcom.services

import org.delcom.data.BaseResponse
import org.delcom.data.PaginationData
import org.delcom.data.WardrobeRequest
import org.delcom.entities.WardrobeCategory
import org.delcom.entities.WardrobeItem
import org.delcom.repositories.IWardrobeRepository
import java.util.*
import kotlin.math.ceil

class WardrobeService(private val repository: IWardrobeRepository) {

    suspend fun addWardrobeItem(userId: String, request: WardrobeRequest, imagePath: String?): BaseResponse<WardrobeItem> {
        if (request.name.isBlank()) throw IllegalArgumentException("Nama item wajib diisi")

        val category = try {
            WardrobeCategory.valueOf(request.category.uppercase())
        } catch (e: Exception) {
            throw IllegalArgumentException("Kategori tidak valid")
        }

        val newItem = WardrobeItem(
            id = UUID.randomUUID().toString(),
            name = request.name,
            category = category,
            color = request.color,
            imagePath = imagePath,
            description = request.description,
            createdAt = ""
        )

        repository.insert(userId, newItem)
        return BaseResponse("success", "Item berhasil disimpan", newItem)
    }

    suspend fun getPagedWardrobe(userId: String, page: Int, perPage: Int, search: String?, category: String?, sortBy: String, order: String): BaseResponse<PaginationData<WardrobeItem>> {
        val totalData = repository.countData(userId, search, category)
        val totalPages = if (totalData == 0L) 1 else ceil(totalData.toDouble() / perPage).toInt()
        val offset = ((page - 1) * perPage).toLong()

        val items = repository.findPaged(userId, perPage, offset, search, category, sortBy, order)

        return BaseResponse("success", "Data berhasil dimuat", PaginationData(items, page, totalPages, page < totalPages))
    }

    suspend fun getDetail(id: String, userId: String): BaseResponse<WardrobeItem> {
        val item = repository.findByIdAndUserId(id, userId)
            ?: throw IllegalStateException("Data tidak ditemukan atau akses ditolak")
        return BaseResponse("success", "Detail berhasil dimuat", item)
    }

    suspend fun updateItem(id: String, userId: String, request: WardrobeRequest, imagePath: String?): BaseResponse<String> {
        val existing = repository.findByIdAndUserId(id, userId)
            ?: throw IllegalStateException("Data tidak ditemukan atau akses ditolak")

        val category = WardrobeCategory.valueOf(request.category.uppercase())
        val updated = existing.copy(
            name = request.name,
            category = category,
            color = request.color,
            description = request.description,
            imagePath = imagePath ?: existing.imagePath
        )

        repository.update(id, userId, updated)
        return BaseResponse("success", "Data berhasil diperbarui")
    }

    suspend fun deleteItem(id: String, userId: String): BaseResponse<String> {
        repository.findByIdAndUserId(id, userId) ?: throw IllegalStateException("Akses ditolak")
        repository.delete(id, userId)
        return BaseResponse("success", "Data berhasil dihapus")
    }
}