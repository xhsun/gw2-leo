package me.xhsun.gw2leo.storage.service

interface IUpdateService {
    /**
     * Add new items with the given item IDs to datastore
     * @param ids Item IDs
     */
    suspend fun addItems(vararg ids: Int): Boolean

    /**
     * Update all sellable items in datastore
     */
    suspend fun updateItems(): Boolean

    /**
     * Update material categories with the given IDs
     *
     * Note: If IDs is not provided, this method will update all material categories
     * @param ids Material category IDs
     */
    suspend fun updateMaterialCategories(vararg ids: Int): Boolean

    /**
     * Update material storage in datastore
     *
     * Note: this method will not update material storage's depended data
     */
    suspend fun updateMaterialItems(): Boolean

    /**
     * Update bank storage in datastore
     *
     * Note: this method will not update bank storage's depended data
     */
    suspend fun updateBankItems(): Boolean

    /**
     * Update character storage in datastore
     *
     * Note: this method will not update character storage's depended data
     * @param characterName Name of the Character
     */
    suspend fun updateInventoryItems(characterName: String): Boolean
}