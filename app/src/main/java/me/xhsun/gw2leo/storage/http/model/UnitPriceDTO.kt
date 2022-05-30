package me.xhsun.gw2leo.storage.http.model

import com.squareup.moshi.Json

data class UnitPriceDTO(
    @Json(name = "unit_price") val unitPrice: Int,
    val quantity: Int
)
