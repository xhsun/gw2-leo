package me.xhsun.gw2leo.storage.http.model

import com.squareup.moshi.Json
import me.xhsun.gw2leo.storage.Item

data class ItemDTO(
    val id: Int,
    @Json(name = "chat_link") val chatLink: String,
    val name: String,
    val icon: String,
    val description: String,
    val rarity: String,
    val level: Int,
    val flags: List<String>
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
            sellable = !flags.contains("NoSell"),
            buy = 0,
            buyGold = 0,
            buySilver = 0,
            buyCopper = 0,
            sell = 0,
            sellGold = 0,
            sellSilver = 0,
            sellCopper = 0
        )
    }
}
