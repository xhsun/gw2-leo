package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.xhsun.gw2leo.storage.MaterialCategory

@Entity
data class MaterialType(
    @PrimaryKey val id: Int,
    val name: String
) {
    fun toDomain(): MaterialCategory {
        return MaterialCategory(
            id = id,
            name = name
        )
    }
}
