package me.xhsun.gw2leo.storage.service.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.config.MATERIAL_STORAGE_PREFIX
import me.xhsun.gw2leo.refresh.service.IStorageRefreshService
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MaterialStorageRemoteMediator @Inject constructor(
    private val accountService: IAccountService,
    private val refreshService: IStorageRefreshService
) : RemoteMediator<Int, MaterialStorage>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MaterialStorage>
    ): MediatorResult {
        return try {
            val storageType = MATERIAL_STORAGE_PREFIX.format(accountService.accountID())
            if (loadType != LoadType.REFRESH || refreshService.shouldUpdate(storageType)) {
                MediatorResult.Success(endOfPaginationReached = true)
            }

            refreshService.updateMaterial()
            Timber.d("Successfully refreshed material storage items")
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            Timber.d("Encountered an error while loading material storage data::${e.message}")
            MediatorResult.Error(e)
        }
    }
}