package me.xhsun.gw2leo.storage.service.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import me.xhsun.gw2leo.core.refresh.service.IStorageRefreshService
import me.xhsun.gw2leo.storage.datastore.entity.Storage
import javax.inject.Inject

class StorageRemoteMediatorBuilder @Inject constructor(
    private val refreshService: IStorageRefreshService
) : IStorageRemoteMediatorBuilder {
    @OptIn(ExperimentalPagingApi::class)
    override fun build(storageType: String): RemoteMediator<Int, Storage> {
        return StorageRemoteMediator(storageType, refreshService)
    }
}