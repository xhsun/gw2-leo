package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.Embedded
import androidx.room.Relation

data class Storage(
    @Embedded val storage: StorageBase,
    @Relation(
        parentColumn = "itemID",
        entityColumn = "id"
    )
    val item: Item,
    @Relation(
        parentColumn = "characterName",
        entityColumn = "name"
    )
    val character: Character
)
