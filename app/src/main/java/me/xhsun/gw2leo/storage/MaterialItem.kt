package me.xhsun.gw2leo.storage

import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorageBase

data class MaterialItem(
    val id: Int,
    val accountID: String,
    val category: MaterialCategory,
    val detail: Item,
    val accountBounded: Boolean,
    val count: Int
) {
    fun toDAO(): MaterialStorageBase {
        return MaterialStorageBase(
            id = id,
            itemID = detail.id,
            accountID = accountID,
            categoryID = category.id,
            accountBounded = accountBounded,
            count = count
        )
    }

    fun update(item: Item, category: MaterialCategory): MaterialItem {
        return MaterialItem(
            id = id,
            accountID = accountID,
            detail = item,
            category = category,
            accountBounded = accountBounded,
            count = count
        )
    }
}
