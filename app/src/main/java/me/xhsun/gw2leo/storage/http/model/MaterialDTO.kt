package me.xhsun.gw2leo.storage.http.model

import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.MaterialCategory
import me.xhsun.gw2leo.storage.MaterialItem

data class MaterialDTO(
    val id: Int,
    val category: Int,
    val binding: String, //Account or omitted
    val count: Int
) {
    fun toDomain(accountID: String): MaterialItem {
        return MaterialItem(
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
                buyGold = 0,
                buySilver = 0,
                buyCopper = 0,
                sellGold = 0,
                sellSilver = 0,
                sellCopper = 0
            ),
            accountBounded = binding.isNotEmpty(),
            count = count
        )
    }
}
