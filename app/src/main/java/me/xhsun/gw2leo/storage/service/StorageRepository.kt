package me.xhsun.gw2leo.storage.service

import androidx.paging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.xhsun.gw2leo.core.config.MAX_RESPONSE_SIZE
import me.xhsun.gw2leo.core.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.StorageState
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import me.xhsun.gw2leo.storage.service.mediator.IStorageRemoteMediatorBuilder
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StorageRepository @Inject constructor(
    private val datastore: IDatastoreRepository,
    private val storageRemoteMediatorBuilder: IStorageRemoteMediatorBuilder,
    private val materialStorageRemoteMediator: RemoteMediator<Int, MaterialStorage>
) : IStorageRepository {

    override fun storageStream(
        storageType: String,
        storageState: StorageState,
        scope: CoroutineScope
    ): Flow<PagingData<StorageItem>> {
        var isBuy = true
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = MAX_RESPONSE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = storageRemoteMediatorBuilder.build(storageType),
            pagingSourceFactory = {
                Timber.d("Display storage items::$storageType::$storageState")
                when (storageState) {
                    StorageState.SELL_ASC_SELLABLE -> {
                        isBuy = false
                        datastore.storageDAO.getAllOderBySellAscendingSellable(storageType)
                    }
                    StorageState.SELL_ASC -> {
                        isBuy = false
                        datastore.storageDAO.getAllOderBySellAscending(storageType)
                    }
                    StorageState.SELL_DESC_SELLABLE -> {
                        isBuy = false
                        datastore.storageDAO.getAllOderBySellDescendingSellable(storageType)
                    }
                    StorageState.SELL_DESC -> {
                        isBuy = false
                        datastore.storageDAO.getAllOderBySellDescending(storageType)
                    }
                    StorageState.BUY_ASC_SELLABLE -> {
                        datastore.storageDAO.getAllOderByBuyAscendingSellable(storageType)
                    }
                    StorageState.BUY_ASC -> {
                        datastore.storageDAO.getAllOderByBuyAscending(storageType)
                    }
                    StorageState.BUY_DESC_SELLABLE -> {
                        datastore.storageDAO.getAllOderByBuyDescendingSellable(storageType)
                    }
                    else -> {
                        datastore.storageDAO.getAllOderByBuyDescending(storageType)
                    }
                }
            }
        ).flow.map { data ->
            data.map { it.toDomain(isBuy) }
        }.cachedIn(scope)
    }


    override fun materialStorageData(
        storageState: StorageState,
        scope: CoroutineScope
    ): Flow<PagingData<StorageItem>> {
        var isBuy = true
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = MAX_RESPONSE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = materialStorageRemoteMediator,
            pagingSourceFactory = {
                Timber.d("Display material storage items::$storageState")
                when (storageState) {
                    StorageState.SELL_ASC_SELLABLE -> {
                        isBuy = false
                        datastore.materialStorageDAO.getAllOderBySellAscendingSellable()
                    }
                    StorageState.SELL_ASC -> {
                        isBuy = false
                        datastore.materialStorageDAO.getAllOderBySellAscending()
                    }
                    StorageState.SELL_DESC_SELLABLE -> {
                        isBuy = false
                        datastore.materialStorageDAO.getAllOderBySellDescendingSellable()
                    }
                    StorageState.SELL_DESC -> {
                        isBuy = false
                        datastore.materialStorageDAO.getAllOderBySellDescending()
                    }
                    StorageState.BUY_ASC_SELLABLE -> {
                        datastore.materialStorageDAO.getAllOderByBuyAscendingSellable()
                    }
                    StorageState.BUY_ASC -> {
                        datastore.materialStorageDAO.getAllOderByBuyAscending()
                    }
                    StorageState.BUY_DESC_SELLABLE -> {
                        datastore.materialStorageDAO.getAllOderByBuyDescendingSellable()
                    }
                    else -> {
                        datastore.materialStorageDAO.getAllOderByBuyDescending()
                    }
                }
            }
        ).flow.map { data ->
            data.map { it.toDomain(isBuy) }
        }.cachedIn(scope)
    }
}