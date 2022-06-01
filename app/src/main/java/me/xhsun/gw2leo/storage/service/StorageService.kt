package me.xhsun.gw2leo.storage.service

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.service.ICharacterService
import me.xhsun.gw2leo.config.DB_BANK_KEY_FORMAT
import me.xhsun.gw2leo.config.DEFAULT_RESPONSE_SIZE
import me.xhsun.gw2leo.config.MATERIAL_STORAGE_KEY
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.http.StoragePagingSource
import timber.log.Timber
import javax.inject.Inject

class StorageService @Inject constructor(
    private val updateService: UpdateService,
    private val datastore: IDatastoreRepository,
    private val accountService: IAccountService,
    private val characterService: ICharacterService,
    private val storageRetrievalService: IStorageRetrievalService
) : IStorageService {

    fun storageStream(storageType: String): LiveData<PagingData<StorageItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_RESPONSE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoragePagingSource(storageType, storageRetrievalService) }
        ).liveData
    }

//    override fun storageData(storageType: String): LiveData<List<StorageItem>> {
//        return Transformations.map(datastore.storageDAO.getAll(storageType)) { list ->
//            list.map {
//                it.toDomain()
//            }
//        }
//    }
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

    override suspend fun updatePrices(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                updateService.updateItems()
            } catch (e: Exception) {
                Timber.d("Failed to update item prices::${e.message}")
                false
            }
        }
    }

    override suspend fun updateAll(): Boolean {
        try {
            withContext(Dispatchers.IO) {
                updateService.updateBankItems()
                updateService.updateMaterialItems()
                characterService.characters().forEach {
                    updateService.updateInventoryItems(it)
                }
            }
        } catch (e: Exception) {
            when (e) {
                is NotLoggedInError -> throw e
                else -> {
                    Timber.d("Failed to update all storages::${e.message}")
                    return false
                }
            }
        }
        return true
    }

    override suspend fun updateStorage(storageType: String): Boolean {
        if (storageType.isEmpty()) {
            throw IllegalArgumentException("storageType")
        }
        return withContext(Dispatchers.IO) {
            when (storageType) {
                DB_BANK_KEY_FORMAT.format(accountService.accountID()) -> updateService.updateBankItems()
                MATERIAL_STORAGE_KEY -> updateService.updateMaterialItems()
                else -> updateService.updateInventoryItems(storageType)
            }
        }
    }
}