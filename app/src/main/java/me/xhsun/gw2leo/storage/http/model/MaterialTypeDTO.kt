package me.xhsun.gw2leo.storage.http.model

import me.xhsun.gw2leo.storage.MaterialCategory

data class MaterialTypeDTO(
    val id: Int,
    val name: String,
    val order: Int
) {
    fun toDomain(): MaterialCategory {
        return MaterialCategory(
            id = id,
            name = name
        )
    }
}
