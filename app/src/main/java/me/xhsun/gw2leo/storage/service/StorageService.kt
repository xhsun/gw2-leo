package me.xhsun.gw2leo.storage.service

import androidx.paging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.xhsun.gw2leo.config.MAX_RESPONSE_SIZE
import me.xhsun.gw2leo.config.ORDER_BY_BUY
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StorageService @Inject constructor(
    private val datastore: IDatastoreRepository,
    private val storageRemoteMediatorBuilder: IStorageRemoteMediatorBuilder,
    private val materialStorageRemoteMediator: RemoteMediator<Int, MaterialStorage>
) : IStorageService {

    override fun storageStream(
        storageType: String,
        orderBy: String,
        isAsc: Boolean,
        scope: CoroutineScope
    ): Flow<PagingData<StorageItem>> {
        val isBuy = orderBy == ORDER_BY_BUY
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = MAX_RESPONSE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = storageRemoteMediatorBuilder.build(storageType),
            pagingSourceFactory = {
                Timber.d("Display storage items::$orderBy::Ascending($isAsc)")
                if (isBuy) {
                    if (isAsc) {
                        datastore.storageDAO.getAllOderByBuyAscending(storageType)
                    } else {
                        datastore.storageDAO.getAllOderByBuyDescending(storageType)
                    }
                } else {
                    if (isAsc) {
                        datastore.storageDAO.getAllOderBySellAscending(storageType)
                    } else {
                        datastore.storageDAO.getAllOderBySellDescending(storageType)
                    }
                }
            }
        ).flow.map { data ->
            data.map { it.toDomain(isBuy) }
        }.cachedIn(scope)
    }


    override fun materialStorageData(
        orderBy: String,
        isAsc: Boolean,
        scope: CoroutineScope
    ): Flow<PagingData<StorageItem>> {
        val isBuy = orderBy == ORDER_BY_BUY
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = MAX_RESPONSE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = materialStorageRemoteMediator,
            pagingSourceFactory = {
                Timber.d("Display storage items::$orderBy::Ascending($isAsc)")
                if (isBuy) {
                    if (isAsc) {
                        datastore.materialStorageDAO.getAllOderByBuyAscending()
                    } else {
                        datastore.materialStorageDAO.getAllOderByBuyDescending()
                    }
                } else {
                    if (isAsc) {
                        datastore.materialStorageDAO.getAllOderBySellAscending()
                    } else {
                        datastore.materialStorageDAO.getAllOderBySellDescending()
                    }
                }
            }
        ).flow.map { data ->
            data.map { it.toDomain(isBuy) }
        }.cachedIn(scope)
    }
}