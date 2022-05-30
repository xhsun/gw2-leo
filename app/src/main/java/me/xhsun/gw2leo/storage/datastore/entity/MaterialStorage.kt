package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.Embedded
import androidx.room.Relation

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
)