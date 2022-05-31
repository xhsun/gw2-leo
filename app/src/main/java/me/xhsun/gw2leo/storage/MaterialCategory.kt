package me.xhsun.gw2leo.storage

import me.xhsun.gw2leo.storage.datastore.entity.MaterialType

data class MaterialCategory(
    val id: Int,
    val name: String
) {
    fun toDAO(): MaterialType {
        return MaterialType(
            id = id,
            name = name
        )
    }
}
