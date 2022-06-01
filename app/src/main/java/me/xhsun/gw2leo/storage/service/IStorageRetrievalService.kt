package me.xhsun.gw2leo.storage.service

import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.MaterialItem
import me.xhsun.gw2leo.storage.StorageItem

interface IStorageRetrievalService {
    /**
     * Get account bank storage items
     * @param current Current page to retrieve
     * @param pageSize Total Page size
     * @return true if at last page and list of storage items
     */
    suspend fun bankItems(current: Int, pageSize: Int): Pair<Boolean, List<StorageItem>>

    /**
     * Get given character's inventory items
     * @param characterName Name of the character
     * @param current Current page to retrieve
     * @param pageSize Total Page size
     * @return true if at last page and list of storage items
     */
    suspend fun inventoryItems(
        characterName: String,
        current: Int,
        pageSize: Int
    ): Pair<Boolean, List<StorageItem>>

    /**
     * Get account material storage items
     * @param current Current page to retrieve
     * @param pageSize Total Page size
     * @return true if at last page and list of material storage items
     */
    suspend fun materialItems(current: Int, pageSize: Int): Pair<Boolean, List<MaterialItem>>

    /**
     * Get full item details for the given ids
     * @param ids Item IDs
     * @return List of item information
     */
    suspend fun fullItemDetails(vararg ids: Int): List<Item>
}