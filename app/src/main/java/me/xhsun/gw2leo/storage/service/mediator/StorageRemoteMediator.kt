package me.xhsun.gw2leo.storage.service.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import me.xhsun.gw2leo.core.refresh.service.IStorageRefreshService
import me.xhsun.gw2leo.storage.datastore.entity.Storage
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StorageRemoteMediator @Inject constructor(
    private val storageType: String,
    private val refreshService: IStorageRefreshService
) : RemoteMediator<Int, Storage>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Storage>
    ): MediatorResult {
        return try {
            if (loadType != LoadType.REFRESH || !refreshService.shouldUpdate(storageType)) {
                MediatorResult.Success(endOfPaginationReached = true)
            } else {
                refreshService.updateStorage(storageType)
                Timber.d("Successfully refreshed storage items::${loadType.name}::$storageType")
                MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            Timber.d("Encountered an error while loading storage data::${e.message}")
            MediatorResult.Error(e)
        }
    }
}