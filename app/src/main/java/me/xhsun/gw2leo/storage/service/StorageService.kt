package me.xhsun.gw2leo.storage.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.*
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.config.DEFAULT_RESPONSE_SIZE
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.StorageItem
import javax.inject.Inject

class StorageService @Inject constructor(
    private val datastore: IDatastoreRepository,
    private val accountService: IAccountService,
    private val storageRetrievalService: IStorageRetrievalService
) : IStorageService {

    override fun storageStream(
        storageType: String,
        orderBy: String,
        isAsc: Boolean
    ): LiveData<PagingData<StorageItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_RESPONSE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = StorageRemoteMediator(
                storageType,
                accountService,
                storageRetrievalService,
                datastore
            ),
            pagingSourceFactory = {
                if (isAsc) {
                    datastore.storageDAO.getAllAscending(storageType, orderBy)
                } else {
                    datastore.storageDAO.getAllDescending(storageType, orderBy)
                }
            }
        ).liveData.map { data ->
            data.map { it.toDomain() }
        }
    }


//
//    override fun materialStorageData(): LiveData<List<MaterialItem>> {
//        val accountID = accountService.accountID()
//        Timber.d("Retrieving material storage information::${accountID}")
//        return Transformations.map(datastore.materialStorageDAO.getAll(accountID)) { list ->
//            list.filter {
//                it.material.count > 0
//            }.map {
//                it.toDomain()
//            }
//        }
//    }

//    override suspend fun updatePrices(): Boolean {
//        return withContext(Dispatchers.IO) {
//            try {
//                updateService.updateItems()
//            } catch (e: Exception) {
//                Timber.d("Failed to update item prices::${e.message}")
//                false
//            }
//        }
//    }
//
//    override suspend fun updateAll(): Boolean {
//        try {
//            withContext(Dispatchers.IO) {
//                updateService.updateBankItems()
//                updateService.updateMaterialItems()
//                characterService.characters().forEach {
//                    updateService.updateInventoryItems(it)
//                }
//            }
//        } catch (e: Exception) {
//            when (e) {
//                is NotLoggedInError -> throw e
//                else -> {
//                    Timber.d("Failed to update all storages::${e.message}")
//                    return false
//                }
//            }
//        }
//        return true
//    }
//
//    override suspend fun updateStorage(storageType: String): Boolean {
//        if (storageType.isEmpty()) {
//            throw IllegalArgumentException("storageType")
//        }
//        return withContext(Dispatchers.IO) {
//            when (storageType) {
//                DB_BANK_KEY_FORMAT.format(accountService.accountID()) -> updateService.updateBankItems()
//                MATERIAL_STORAGE_KEY -> updateService.updateMaterialItems()
//                else -> updateService.updateInventoryItems(storageType)
//            }
//        }
//    }
}