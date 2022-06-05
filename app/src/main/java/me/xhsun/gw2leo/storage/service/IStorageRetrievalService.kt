package me.xhsun.gw2leo.storage.service

import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.StorageItem

interface IStorageRetrievalService {
    /**
     * Get account bank storage items
     * @return list of storage items
     */
    suspend fun bankItems(): List<StorageItem>

    /**
     * Get given character's inventory items
     * @param characterName Name of the character
     * @return list of storage items
     */
    suspend fun inventoryItems(characterName: String): List<StorageItem>

    /**
     * Get account material storage items
     * @return list of material storage items
     */
    suspend fun materialItems(): List<StorageItem>

    /**
     * Get full item details for the given ids
     * @param ids Item IDs
     * @return List of item information
     */
    suspend fun fullItemDetails(ids: Set<Int>): Map<Int, Item>
}