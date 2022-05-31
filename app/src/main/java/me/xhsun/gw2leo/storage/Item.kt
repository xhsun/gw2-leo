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
    val buy: Int,
    val buyGold: Int,
    val buySilver: Int,
    val buyCopper: Int,
    val sell: Int,
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
            buy = buy,
            buyGold = buyGold,
            buySilver = buySilver,
            buyCopper = buyCopper,
            sell = sell,
            sellGold = sellGold,
            sellSilver = sellSilver,
            sellCopper = sellCopper
        )
    }

    fun updatePrice(
        buy: Int,
        sell: Int
    ): Item {
        val buyPrice = this.parseCoins(buy)
        val sellPrice = this.parseCoins(sell)
        return Item(
            id = id,
            chatLink = chatLink,
            name = name,
            icon = icon,
            description = description,
            rarity = rarity,
            level = level,
            sellable = sellable,
            buy = buy,
            buyGold = buyPrice.first,
            buySilver = buyPrice.second,
            buyCopper = buyPrice.third,
            sell = sell,
            sellGold = sellPrice.first,
            sellSilver = sellPrice.second,
            sellCopper = sellPrice.third
        )
    }

    /**
     * Convert given value to coins
     * @return Triple<gold, silver, copper>
     */
    private fun parseCoins(value: Int): Triple<Int, Int, Int> {
        if (value < 0) return Triple(0, 0, 0)
        var temp = value
        val copper = temp % 100
        temp /= 100
        val silver = temp % 100
        val gold = temp / 100
        return Triple(gold, silver, copper)
    }

}
