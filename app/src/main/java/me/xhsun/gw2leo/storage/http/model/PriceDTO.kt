package me.xhsun.gw2leo.storage.http.model

data class PriceDTO(
    val id: Int,
    val whitelisted: Boolean,
    val buys: UnitPriceDTO,
    val sells: UnitPriceDTO
)
