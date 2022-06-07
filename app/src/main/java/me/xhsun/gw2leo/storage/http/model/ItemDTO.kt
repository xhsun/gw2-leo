package me.xhsun.gw2leo.storage.http.model

import com.squareup.moshi.Json
import me.xhsun.gw2leo.storage.Item

data class ItemDTO(
    val id: Int,
    @Json(name = "chat_link") val chatLink: String,
    val name: String,
    val icon: String,
    val type: String,
    val description: String?,
    val rarity: String,
    val level: Int,
    val flags: List<String>,
    val details: ItemDetailDTO?
) {
    fun toDomain(): Item {
        var description = details?.asString() ?: ""
        if (this.description != null) {
            description += if (description.isEmpty()) this.description else "<br>${this.description}"
        }
        description = description.trim().replace("\n", "<br>")
        return Item(
            id = id,
            chatLink = chatLink,
            name = name,
            icon = icon,
            type = type,
            description = description,
            rarity = rarity,
            level = level,
            sellable = true,
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
