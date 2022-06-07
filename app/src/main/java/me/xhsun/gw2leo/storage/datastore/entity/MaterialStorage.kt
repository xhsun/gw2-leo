package me.xhsun.gw2leo.storage.datastore.entity

import me.xhsun.gw2leo.config.MATERIAL_STORAGE_PREFIX
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.MaterialCategory
import me.xhsun.gw2leo.storage.StorageItem

data class MaterialStorage(
    val id: Int,
    val itemID: Int,
    val accountID: String,
    val categoryID: Int,
    val binding: String,
    val count: Int,
    val categoryCategoryID: String,
    val categoryName: String,
    val itemItemID: Int,
    val chatLink: String,
    val name: String,
    val icon: String,
    val type: String,
    val description: String,
    val rarity: String,
    val level: Int,
    val sellable: Boolean,
    val buy: Int,
    val buyGold: Int,
    val buySilver: Int,
    val buyCopper: Int,
    val sell: Int,
    val sellGold: Int,
    val sellSilver: Int,
    val sellCopper: Int
) {
    fun toDomain(isBuy: Boolean): StorageItem {
        var sellable = false
        var price = 0
        var gold = 0
        var silver = 0
        var copper = 0
        if (binding.isEmpty()) {
            sellable = this.sellable
            price = if (isBuy) buy else sell
            gold = if (isBuy) buyGold else sellGold
            silver = if (isBuy) buySilver else sellSilver
            copper = if (isBuy) buyCopper else sellCopper
        }

        return StorageItem(
            id = id,
            accountID = accountID,
            binding = binding,
            count = count,
            category = MaterialCategory(
                id = categoryID,
                name = categoryName
            ),
            detail = Item(
                id = itemID,
                chatLink = chatLink,
                name = name,
                icon = icon,
                description = description,
                rarity = rarity,
                level = level,
                sellable = sellable,
                buy = buy,
                buyGold = buyGold,
                buySilver = buySilver,
                buyCopper = buyCopper,
                sell = sell,
                sellGold = sellGold,
                sellSilver = sellSilver,
                sellCopper = sellCopper,
                type = type
            ),
            storageType = MATERIAL_STORAGE_PREFIX,
            bindTo = "",
            charges = null,
            price = price,
            gold = gold,
            silver = silver,
            copper = copper
        )
    }
}