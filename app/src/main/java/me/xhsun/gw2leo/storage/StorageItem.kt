package me.xhsun.gw2leo.storage

import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorageBase
import me.xhsun.gw2leo.storage.datastore.entity.StorageBase

data class StorageItem(
    val id: Int,
    val accountID: String,
    val category: MaterialCategory?,
    val detail: Item,
    val storageType: String,
    val count: Int,
    val charges: Int?,
    val binding: String,
    val bindTo: String?,
    val price: Int,
    val gold: Int,
    val silver: Int,
    val copper: Int,
) {
    fun toMaterialDAO(): MaterialStorageBase {
        return MaterialStorageBase(
            id = id,
            itemID = detail.id,
            accountID = accountID,
            categoryID = category!!.id,
            binding = binding,
            count = count
        )
    }

    fun toStorageDAO(): StorageBase {
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

    fun update(item: Item, category: MaterialCategory?): StorageItem {
        return StorageItem(
            id = id,
            accountID = accountID,
            detail = item,
            category = category,
            storageType = storageType,
            charges = charges,
            binding = binding,
            bindTo = bindTo,
            count = count,
            price = price,
            gold = gold,
            silver = silver,
            copper = copper
        )
    }
}
