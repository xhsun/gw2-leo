package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.Embedded
import androidx.room.Relation
import me.xhsun.gw2leo.storage.MaterialItem

data class MaterialStorage(
    @Embedded val material: MaterialStorageBase,
    @Relation(
        parentColumn = "categoryID",
        entityColumn = "id"
    )
    val type: MaterialType,
    @Relation(
        parentColumn = "itemID",
        entityColumn = "id"
    )
    val item: Item
) {
    fun toDomain(): MaterialItem {
        return MaterialItem(
            id = material.id,
            accountID = material.accountID,
            category = type.toDomain(),
            detail = item.toDomain(),
            accountBounded = material.accountBounded,
            count = material.count
        )
    }
}