package me.xhsun.gw2leo.storage.service

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import me.xhsun.gw2leo.storage.datastore.entity.Storage

interface IStorageRemoteMediatorBuilder {
    @OptIn(ExperimentalPagingApi::class)
    fun build(storageType: String): RemoteMediator<Int, Storage>
}