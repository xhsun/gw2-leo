package me.xhsun.gw2leo.storage

data class StorageItem(
    val id: Int,
    val detail: Item,
    val storageType: String,
    val count: Int,
    val charges: Int,
    val binding: String,
    val bindTo: String?
)
