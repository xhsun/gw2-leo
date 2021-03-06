package me.xhsun.gw2leo.storage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.xhsun.gw2leo.core.config.*

@Parcelize
data class Item(
    val id: Int,
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
) : Parcelable {
    fun toDAO(): me.xhsun.gw2leo.storage.datastore.entity.Item {
        return me.xhsun.gw2leo.storage.datastore.entity.Item(
            id = id,
            chatLink = chatLink,
            name = name,
            icon = icon,
            type = type,
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
        sell: Int,
        sellable: Boolean = true
    ): Item {
        val buyPrice = parseCoins(buy)
        val sellPrice = parseCoins(sell)
        return Item(
            id = id,
            chatLink = chatLink,
            name = name,
            icon = icon,
            type = type,
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

    override fun equals(other: Any?) = (other is Item) && id == other.id

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + chatLink.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + rarity.hashCode()
        result = 31 * result + level
        result = 31 * result + sellable.hashCode()
        result = 31 * result + buy
        result = 31 * result + buyGold
        result = 31 * result + buySilver
        result = 31 * result + buyCopper
        result = 31 * result + sell
        result = 31 * result + sellGold
        result = 31 * result + sellSilver
        result = 31 * result + sellCopper
        return result
    }

    companion object {
        /**
         * Convert given value to coins
         * @return Triple<gold, silver, copper>
         */
        @JvmStatic
        fun parseCoins(value: Int): Triple<Int, Int, Int> {
            if (value <= 0) return Triple(0, 0, 0)
            var temp = value
            val copper = temp % 100
            temp /= 100
            val silver = temp % 100
            val gold = temp / 100
            return Triple(gold, silver, copper)
        }

        /**
         * Convert given rarity to color code
         */
        @JvmStatic
        fun getColorCode(rarity: String): Int {
            return when (rarity) {
                "Junk" -> COLOR_Junk
                "Fine" -> COLOR_Fine
                "Masterwork" -> COLOR_Masterwork
                "Rare" -> COLOR_Rare
                "Exotic" -> COLOR_Exotic
                "Ascended" -> COLOR_Ascended
                "Legendary" -> COLOR_Legendary
                else -> COLOR_Basic
            }
        }
    }
}


