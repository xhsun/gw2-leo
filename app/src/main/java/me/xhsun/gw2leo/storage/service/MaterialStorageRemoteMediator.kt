package me.xhsun.gw2leo.storage.service

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.config.MATERIAL_STORAGE_KEY_FORMAT
import me.xhsun.gw2leo.config.MIN_REFRESH_RATE_HR
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.datastore.entity.LastModified
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MaterialStorageRemoteMediator @Inject constructor(
    private val accountService: IAccountService,
    private val storageRetrievalService: IStorageRetrievalService,
    private val datastore: IDatastoreRepository
) : RemoteMediator<Int, MaterialStorage>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MaterialStorage>
    ): MediatorResult {
        if (loadType != LoadType.REFRESH) {
            MediatorResult.Success(endOfPaginationReached = true)
        }

        try {
            val accountID = accountService.accountID()
            val storageType = MATERIAL_STORAGE_KEY_FORMAT.format(accountID)
            val lastModified = datastore.lastModifiedDAO.get(storageType)
            if (lastModified != null && lastModified.elapsedTime() < MIN_REFRESH_RATE_HR) {
                Timber.d("material storage item list was recently updated, skip::${loadType.name}::$lastModified")
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val items = storageRetrievalService.materialItems()

            datastore.withTransaction {
                datastore.materialStorageDAO.bulkDelete(accountID)
                datastore.itemDAO.insertAll(items.map { it.detail.toDAO() })
                datastore.materialTypeDAO.insertAll(items.map { it.category!!.toDAO() })
                datastore.materialStorageDAO.insertAll(items.map { it.toMaterialDAO() })
                datastore.lastModifiedDAO.insert(
                    LastModified(
                        storageType,
                        System.currentTimeMillis()
                    )
                )
            }
            Timber.d("Successfully refreshed material storage items::${items.size}")
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            Timber.d("Encountered an error while loading material storage data::${e.message}")
            return MediatorResult.Error(e)
        }
    }
}