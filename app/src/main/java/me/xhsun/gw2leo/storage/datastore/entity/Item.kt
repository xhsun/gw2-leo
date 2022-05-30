package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.xhsun.gw2leo.storage.Item

@Entity
data class Item(
    @PrimaryKey val id: Int,
    val chatLink: String,
    val name: String,
    val icon: String,
    val description: String,
    val rarity: String,
    val level: Int,
    @ColumnInfo(defaultValue = "true") val sellable: Boolean,
    @ColumnInfo(defaultValue = "0") val buyGold: Int,
    @ColumnInfo(defaultValue = "0") val buySilver: Int,
    @ColumnInfo(defaultValue = "0") val buyCopper: Int,
    @ColumnInfo(defaultValue = "0") val sellGold: Int,
    @ColumnInfo(defaultValue = "0") val sellSilver: Int,
    @ColumnInfo(defaultValue = "0") val sellCopper: Int
) {
    fun toDomain(): Item {
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