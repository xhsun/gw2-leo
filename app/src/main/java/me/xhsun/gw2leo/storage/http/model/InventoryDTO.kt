package me.xhsun.gw2leo.storage.http.model

import com.squareup.moshi.Json

data class InventoryDTO(
    val id: Int,
    val count: Int,
    val charges: Int,
    val binding: String,
    @Json(name = "bound_to") val boundTo: String
)
