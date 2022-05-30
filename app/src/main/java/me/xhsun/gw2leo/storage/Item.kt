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
)
