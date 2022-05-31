package me.xhsun.gw2leo.storage

import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorageBase

data class MaterialItem(
    val accountID: String,
    val category: MaterialCategory,
    val detail: Item,
    val accountBounded: Boolean,
    val count: Int
) {
    fun toDAO(): MaterialStorageBase {
        return MaterialStorageBase(
            itemID = detail.id,
            accountID = accountID,
            categoryID = category.id,
            accountBounded = accountBounded,
            count = count
        )
    }
}
