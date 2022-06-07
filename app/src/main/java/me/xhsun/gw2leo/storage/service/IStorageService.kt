package me.xhsun.gw2leo.storage.service

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.StorageState

interface IStorageService {
    /**
     * @param storageType Either character name or formatted bank key
     * @param storageState Filtering/sorting state
     * @param scope Scope to cache result in
     * @return Storage paging data
     */
    fun storageStream(
        storageType: String,
        storageState: StorageState,
        scope: CoroutineScope
    ): Flow<PagingData<StorageItem>>

    /**
     * @param storageState Filtering/sorting state
     * @param scope Scope to cache result in
     * @return Material storage paging data
     */
    fun materialStorageData(
        storageState: StorageState,
        scope: CoroutineScope
    ): Flow<PagingData<StorageItem>>
}