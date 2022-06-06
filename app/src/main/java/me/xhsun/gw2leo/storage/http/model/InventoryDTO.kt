package me.xhsun.gw2leo.storage.http.model

import com.squareup.moshi.Json
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.StorageItem

data class InventoryDTO(
    val id: Int,
    val count: Int,
    val charges: Int?,
    val binding: String?,
    @Json(name = "bound_to") val boundTo: String?
) {
    fun toDomain(storageType: String): StorageItem {
        return StorageItem(
            id = 0,
            detail = Item(
                id = id,
                chatLink = "",
                name = "",
                icon = "",
                description = "",
                rarity = "",
                level = 0,
                sellable = false,
                buy = 0,
                buyGold = 0,
                buySilver = 0,
                buyCopper = 0,
                sell = 0,
                sellGold = 0,
                sellSilver = 0,
                sellCopper = 0
            ),
            storageType = storageType,
            binding = binding ?: "",
            bindTo = boundTo,
            charges = charges,
            count = count,
            accountID = "",
            category = null,
            price = 0,
            gold = 0,
            silver = 0,
            copper = 0
        )
    }
}
