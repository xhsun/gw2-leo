package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.Embedded
import androidx.room.Relation
import me.xhsun.gw2leo.account.datastore.entity.Character

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
