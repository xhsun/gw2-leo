package me.xhsun.gw2leo.core.refresh.service

import androidx.room.withTransaction
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.service.ICharacterService
import me.xhsun.gw2leo.core.config.*
import me.xhsun.gw2leo.core.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.datastore.entity.LastModified
import me.xhsun.gw2leo.storage.error.NoItemFoundError
import me.xhsun.gw2leo.storage.service.IStorageRetrievalService
import timber.log.Timber
import javax.inject.Inject

class StorageRefreshService @Inject constructor(
    private val accountService: IAccountService,
    private val datastore: IDatastoreRepository,
    private val characterService: ICharacterService,
    private val storageRetrievalService: IStorageRetrievalService
) : IStorageRefreshService {

    override suspend fun updateAll() {
        val lastModified = emptyList<LastModified>().toMutableList()
        val accountID = accountService.accountID()
        val bankKey = BANK_STORAGE_KEY_FORMAT.format(accountService.accountID())
        val characters = characterService.characters().toMutableList()

        val storageItems = characters.flatMap {
            val res = storageRetrievalService.inventoryItems(it)
            if (res.isNotEmpty()) {
                lastModified.add(LastModified(it, System.currentTimeMillis()))
            }
            res
        }
            .toMutableList()
        val bankItems = storageRetrievalService.bankItems()
        if (bankItems.isNotEmpty()) {
            lastModified.add(LastModified(bankKey, System.currentTimeMillis()))
            storageItems.addAll(bankItems)
        }
        val shouldRefreshStorage = lastModified.map { it.type }

        var shouldRefreshMaterial = false
        val materialItems = storageRetrievalService.materialItems()
        if (materialItems.isNotEmpty()) {
            lastModified.add(
                LastModified(
                    MATERIAL_STORAGE_PREFIX.format(accountID),
                    System.currentTimeMillis()
                )
            )
            shouldRefreshMaterial = true
        }

        val updatedIds = storageItems.filter { it.detail.sellable }.map { it.detail.id }
            .toMutableSet()
        updatedIds.addAll(materialItems.filter { it.detail.sellable }.map { it.detail.id })
        val shouldUpdate = datastore.itemDAO.getAllSellableIds()
            .filterNot { i -> updatedIds.any { it == i } }
        val items = storageItems.map { it.detail }.toMutableList()
        items.addAll(materialItems.map { it.detail })
        if (shouldUpdate.isNotEmpty()) {
            items.addAll(
                storageRetrievalService.fullItemDetails(shouldUpdate.toSet()).values
                    .toMutableSet()
            )
        }

        datastore.withTransaction {
            if (items.isNotEmpty()) {
                datastore.itemDAO.insertAll(items.map { it.toDAO() })
                Timber.d("Successfully updated ${items.size} item entries")
            }

            if (shouldRefreshStorage.isNotEmpty()) {
                datastore.storageDAO.bulkDelete(shouldRefreshStorage)
                datastore.storageDAO.insertAll(storageItems.map { it.toStorageDAO() })
                Timber.d("Successfully updated ${storageItems.size} storage item entries")
            }

            if (shouldRefreshMaterial) {
                datastore.materialStorageDAO.bulkDelete(accountID)
                datastore.materialTypeDAO.insertAll(materialItems.map { it.category!!.toDAO() })
                datastore.materialStorageDAO.insertAll(materialItems.map { it.toMaterialDAO() })
                Timber.d("Successfully updated ${materialItems.size} material item entries")
            }

            if (lastModified.isNotEmpty()) {
                datastore.lastModifiedDAO.insertAll(lastModified)
            }
        }
    }

    override suspend fun updateStorage(storageType: String) {
        val items = if (storageType.contains(BANK_STORAGE_PREFIX)) {
            storageRetrievalService.bankItems()
        } else {
            storageRetrievalService.inventoryItems(storageType)
        }
        if (items.isEmpty()) {
            throw NoItemFoundError()
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
        Timber.d("Successfully updated ${items.size} storage item entries")
    }

    override suspend fun updateMaterial() {
        val accountID = accountService.accountID()
        val items = storageRetrievalService.materialItems()
        if (items.isEmpty()) {
            throw NoItemFoundError()
        }

        datastore.withTransaction {
            datastore.materialStorageDAO.bulkDelete(accountID)
            datastore.itemDAO.insertAll(items.map { it.detail.toDAO() })
            datastore.materialTypeDAO.insertAll(items.map { it.category!!.toDAO() })
            datastore.materialStorageDAO.insertAll(items.map { it.toMaterialDAO() })
            datastore.lastModifiedDAO.insert(
                LastModified(
                    MATERIAL_STORAGE_KEY_FORMAT.format(accountID),
                    System.currentTimeMillis()
                )
            )
        }
        Timber.d("Successfully updated ${items.size} material storage item entries")
    }

    override suspend fun shouldUpdate(storageType: String): Boolean {
        val lastModified = datastore.lastModifiedDAO.get(storageType)
        Timber.d("storage item list last updated in::$lastModified")
        return lastModified == null || lastModified.elapsedTime() > MIN_REFRESH_RATE_HR
    }
}