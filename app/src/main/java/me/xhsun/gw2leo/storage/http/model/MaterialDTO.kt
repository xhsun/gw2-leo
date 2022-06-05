package me.xhsun.gw2leo.storage.http.model

import me.xhsun.gw2leo.config.MATERIAL_STORAGE_PREFIX
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.MaterialCategory
import me.xhsun.gw2leo.storage.StorageItem

data class MaterialDTO(
    val id: Int,
    val category: Int,
    val binding: String?, //Account or omitted
    val count: Int
) {
    fun toDomain(accountID: String): StorageItem {
        return StorageItem(
            id = id,
            accountID = accountID,
            category = MaterialCategory(
                id = category,
                name = ""
            ),
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
            binding = binding ?: "",
            count = count,
            storageType = MATERIAL_STORAGE_PREFIX,
            charges = null,
            bindTo = ""
        )
    }
}
