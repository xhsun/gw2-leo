package me.xhsun.gw2leo.storage.datastore.entity

import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.StorageItem

data class Storage(
    val id: Int,
    val itemID: Int,
    val storageType: String,
    val count: Int,
    val charges: Int,
    val binding: String,
    val bindTo: String?,
    val itemItemID: Int,
    val chatLink: String,
    val name: String,
    val icon: String,
    val description: String,
    val rarity: String,
    val level: Int,
    val sellable: Boolean,
    val type: String,
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
        return StorageItem(
            id = id,
            detail = Item(
                id = itemID,
                chatLink = chatLink,
                name = name,
                icon = icon,
                description = description,
                rarity = rarity,
                level = level,
                sellable = binding.isEmpty() && sellable,
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
            storageType = storageType,
            count = count,
            charges = charges,
            binding = binding,
            bindTo = bindTo,
            accountID = "",
            category = null,
            price = if (isBuy) buy else sell,
            gold = if (isBuy) buyGold else sellGold,
            silver = if (isBuy) buySilver else sellSilver,
            copper = if (isBuy) buyCopper else sellCopper
        )
    }
}
