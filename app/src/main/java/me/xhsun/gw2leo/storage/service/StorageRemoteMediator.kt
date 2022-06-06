package me.xhsun.gw2leo.storage.service

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import me.xhsun.gw2leo.config.BANK_STORAGE_PREFIX
import me.xhsun.gw2leo.datastore.IDatastoreRepository
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
            }
            Timber.d("Successfully refreshed storage items::${loadType.name}::$storageType::${items.size}")
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            Timber.d("Encountered an error while loading storage data::${e.message}")
//            when (e) {// Could also be IOException, HttpException
//                is NotLoggedInError ->  return MediatorResult.Error(e)
//                is NoItemFoundError ->  return MediatorResult.Error(e)
//                is HttpException -> return MediatorResult.Error(e.message)
//            }
//            throw e
            return MediatorResult.Error(e)
        }
    }
}