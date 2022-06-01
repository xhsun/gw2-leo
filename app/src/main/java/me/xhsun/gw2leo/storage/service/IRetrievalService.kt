package me.xhsun.gw2leo.storage.service

import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.MaterialCategory
import me.xhsun.gw2leo.storage.MaterialItem
import me.xhsun.gw2leo.storage.StorageItem

interface IRetrievalService {
    /**
     * Get material category information for the given ids
     * @param ids Material category IDs
     * @return List of material category information
     */
    suspend fun getMaterialCategories(vararg ids: Int): List<MaterialCategory>

    /**
     * Get item information for the given ids
     * @param ids Item IDs
     * @return List of item information
     */
    suspend fun getItems(vararg ids: Int): List<Item>

    /**
     * Get item price information for the given items
     * @param shouldUpdate Items to be updated
     * @return List of updated items
     */
    suspend fun getItemPrices(vararg shouldUpdate: Item): List<Item>

    /**
     * Get account material storage items
     * @return List of material storage items
     */
    suspend fun getMaterialItems(): List<MaterialItem>

    /**
     * Get account bank storage items
     * @return List of storage items
     */
    suspend fun getBankItems(): List<StorageItem>

    /**
     * Get given character's inventory items
     * @param characterName Name of the character
     * @return List of storage items
     */
    suspend fun getInventoryItems(characterName: String): List<StorageItem>
}