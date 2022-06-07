package me.xhsun.gw2leo.core.refresh.service

import me.xhsun.gw2leo.core.config.MIN_REFRESH_RATE_HR

interface IStorageRefreshService {
    /**
     * Update all storages for current account and update all item prices
     */
    suspend fun updateAll()

    /**
     * Update the given storage type
     * @param storageType Storage type
     */
    suspend fun updateStorage(storageType: String)

    /**
     * Update material storage
     */
    suspend fun updateMaterial()

    /**
     * @param storageType Storage type to check
     * @return True if last modified for this storage type is greater than [MIN_REFRESH_RATE_HR], false otherwise
     */
    suspend fun shouldUpdate(storageType: String): Boolean
}