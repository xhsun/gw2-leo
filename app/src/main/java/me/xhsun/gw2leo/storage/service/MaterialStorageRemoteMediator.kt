package me.xhsun.gw2leo.storage.service

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import me.xhsun.gw2leo.storage.error.NoItemFoundError
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
            val items = storageRetrievalService.materialItems()

            datastore.withTransaction {
                datastore.materialStorageDAO.bulkDelete(accountID)
                datastore.itemDAO.insertAll(items.map { it.detail.toDAO() })
                datastore.materialTypeDAO.insertAll(items.map { it.category!!.toDAO() })
                datastore.materialStorageDAO.insertAll(items.map { it.toMaterialDAO() })
            }
            Timber.d("Successfully refreshed material storage items::${items.size}")
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            Timber.d("Encountered an error while loading material storage data::${e.message}")
            when (e) {// Could also be IOException, HttpException
                is NotLoggedInError -> {
                    return MediatorResult.Error(e)
                }
                is NoItemFoundError -> return MediatorResult.Success(endOfPaginationReached = true)
            }
            return MediatorResult.Error(e)
        }
    }
}