package me.xhsun.gw2leo.storage.http.model

import com.squareup.moshi.Json

data class ItemDTO(
    val id: Int,
    @Json(name = "chat_link") val chatLink: String,
    val name: String,
    val icon: String,
    val description: String,
    val rarity: String,
    val level: Int,
    val flags: List<String> //NoSell
)
