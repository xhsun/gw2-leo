package me.xhsun.gw2leo.storage.service

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import me.xhsun.gw2leo.config.BANK_STORAGE_PREFIX
import me.xhsun.gw2leo.config.MIN_REFRESH_RATE_HR
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.datastore.entity.LastModified
import me.xhsun.gw2leo.storage.datastore.entity.Storage
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StorageRemoteMediator @Inject constructor(
    private val storageType: String,
    private val storageRetrievalService: IStorageRetrievalService,
    private val datastore: IDatastoreRepository
) : RemoteMediator<Int, Storage>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Storage>
    ): MediatorResult {
        if (loadType != LoadType.REFRESH) {
            MediatorResult.Success(endOfPaginationReached = true)
        }
        val lastModified = datastore.lastModifiedDAO.get(storageType)
        if (lastModified != null && lastModified.elapsedTime() < MIN_REFRESH_RATE_HR) {
            Timber.d("Storage item list was recently updated, skip::${loadType.name}::$lastModified")
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        val isBank = storageType.contains(BANK_STORAGE_PREFIX)
        try {
            val items = if (isBank) {
                storageRetrievalService.bankItems()
            } else {
                storageRetrievalService.inventoryItems(storageType)
            }

            datastore.withTransaction {
                datastore.storageDAO.bulkDelete(storageType)
                datastore.itemDAO.insertAll(items.map { it.detail.toDAO() })
                datastore.storageDAO.insertAll(items.map { it.toStorageDAO() })
                datastore.lastModifiedDAO.insert(
                    LastModified(
                        storageType,
                        System.currentTimeMillis()
                    )
                )
            }
            Timber.d("Successfully refreshed storage items::${loadType.name}::$storageType::${items.size}")
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            Timber.d("Encountered an error while loading storage data::${e.message}")
            return MediatorResult.Error(e)
        }
    }
}