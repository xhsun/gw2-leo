package me.xhsun.gw2leo.storage.http.model

import com.squareup.moshi.Json

data class ItemDetailDTO(
    val type: String?,
    @Json(name = "weight_class") val weightClass: String?,
    val description: String?,
) {
    fun asString(): String {
        var plain = if (type == null) "" else "$type\n"
        plain += if (weightClass == null) "" else "$weightClass\n"
        plain += if (description == null) "" else "$description\n"
        return plain
    }
}
