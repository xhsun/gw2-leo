package me.xhsun.gw2leo.storage

data class Item(
    val id: Int,
    val chatLink: String,
    val name: String,
    val icon: String,
    val description: String,
    val rarity: String,
    val level: Int,
    val sellable: Boolean,
    val buyGold: Int,
    val buySilver: Int,
    val buyCopper: Int,
    val sellGold: Int,
    val sellSilver: Int,
    val sellCopper: Int
) {
    fun toDAO(): me.xhsun.gw2leo.storage.datastore.entity.Item {
        return me.xhsun.gw2leo.storage.datastore.entity.Item(
            id = id,
            chatLink = chatLink,
            name = name,
            icon = icon,
            description = description,
            rarity = rarity,
            level = level,
            sellable = sellable,
            buyGold = buyGold,
            buySilver = buySilver,
            buyCopper = buyCopper,
            sellGold = sellGold,
            sellSilver = sellSilver,
            sellCopper = sellCopper
        )
    }

    fun updatePrice(
        buyGold: Int,
        buySilver: Int,
        buyCopper: Int,
        sellGold: Int,
        sellSilver: Int,
        sellCopper: Int
    ): Item {
        return Item(
            id = id,
            chatLink = chatLink,
            name = name,
            icon = icon,
            description = description,
            rarity = rarity,
            level = level,
            sellable = sellable,
            buyGold = buyGold,
            buySilver = buySilver,
            buyCopper = buyCopper,
            sellGold = sellGold,
            sellSilver = sellSilver,
            sellCopper = sellCopper
        )
    }
}
