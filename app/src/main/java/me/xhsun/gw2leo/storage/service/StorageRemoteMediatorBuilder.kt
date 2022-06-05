package me.xhsun.gw2leo.storage.service

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.datastore.entity.Storage
import javax.inject.Inject

class StorageRemoteMediatorBuilder @Inject constructor(
    private val storageRetrievalService: IStorageRetrievalService,
    private val datastore: IDatastoreRepository
) : IStorageRemoteMediatorBuilder {
    @OptIn(ExperimentalPagingApi::class)
    override fun build(storageType: String): RemoteMediator<Int, Storage> {
        return StorageRemoteMediator(storageType, storageRetrievalService, datastore)
    }
}