package me.xhsun.gw2leo.storage.service

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import me.xhsun.gw2leo.storage.StorageItem

interface IStorageService {
    /**
     * @param storageType Either character name or formatted bank key
     * @param orderBy Either [ORDER_BY_BUY] or [ORDER_BY_SELL]
     * @param isAsc True to order by ascending, false otherwise
     * @param scope Scope to cache result in
     * @return Storage paging data
     */
    fun storageStream(
        storageType: String,
        orderBy: String,
        isAsc: Boolean,
        scope: CoroutineScope
    ): Flow<PagingData<StorageItem>>

    /**
     * @param orderBy Either [ORDER_BY_BUY] or [ORDER_BY_SELL]
     * @param isAsc True to order by ascending, false otherwise
     * @param scope Scope to cache result in
     * @return Material storage paging data
     */
    fun materialStorageData(
        orderBy: String,
        isAsc: Boolean,
        scope: CoroutineScope
    ): Flow<PagingData<StorageItem>>
}