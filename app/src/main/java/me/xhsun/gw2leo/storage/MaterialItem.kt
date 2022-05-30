package me.xhsun.gw2leo.storage

data class MaterialItem(
    val accountID: String,
    val category: MaterialCategory,
    val detail: Item,
    val accountBounded: Boolean,
    val count: Int
)
