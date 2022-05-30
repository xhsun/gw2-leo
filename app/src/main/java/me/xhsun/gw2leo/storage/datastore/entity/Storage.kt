package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.Embedded
import androidx.room.Relation
import me.xhsun.gw2leo.storage.StorageItem

data class Storage(
    @Embedded val storage: StorageBase,
    @Relation(
        parentColumn = "itemID",
        entityColumn = "id"
    )
    val item: Item
) {
    fun toDomain(): StorageItem {
        return StorageItem(
            id = storage.id,
            detail = item.toDomain(),
            storageType = storage.storageType,
            count = storage.count,
            charges = storage.charges,
            binding = storage.binding,
            bindTo = storage.bindTo
        )
    }
}
