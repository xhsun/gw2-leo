package me.xhsun.gw2leo.storage

import me.xhsun.gw2leo.storage.datastore.entity.StorageBase

data class StorageItem(
    val id: Int,
    val detail: Item,
    val storageType: String,
    val count: Int,
    val charges: Int,
    val binding: String,
    val bindTo: String?
) {
    fun toDAO(): StorageBase {
        return StorageBase(
            id = id,
            itemID = detail.id,
            storageType = storageType,
            charges = charges,
            binding = binding,
            bindTo = bindTo,
            count = count
        )
    }

    fun updateItem(item: Item): StorageItem {
        return StorageItem(
            id = id,
            detail = item,
            storageType = storageType,
            charges = charges,
            binding = binding,
            bindTo = bindTo,
            count = count
        )
    }
}
