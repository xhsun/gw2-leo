package me.xhsun.gw2leo.storage.service

import androidx.lifecycle.LiveData
import me.xhsun.gw2leo.storage.MaterialItem
import me.xhsun.gw2leo.storage.StorageItem

interface IStorageService {
    /**
     * @param storageType Either character name or formatted bank key
     * @return Storage live data for the current storage type
     */
    fun storageData(storageType: String): LiveData<List<StorageItem>>

    /**
     * @return Material storage live data for the current account
     */
    fun materialStorageData(): LiveData<List<MaterialItem>>

    /**
     * Update all sellable item prices
     */
    suspend fun updatePrices(): Boolean

    /**
     * Update all storage type and their dependent categories/items
     */
    suspend fun updateAll(): Boolean

    /**
     * Update given storage type and their dependent categories/items
     * @param storageType Either character name, formatted bank key, or material storage key
     */
    suspend fun updateStorage(storageType: String): Boolean
}