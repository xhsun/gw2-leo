package me.xhsun.gw2leo.core.refresh.service

import androidx.room.withTransaction
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.service.ICharacterService
import me.xhsun.gw2leo.core.config.*
import me.xhsun.gw2leo.core.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.datastore.entity.LastModified
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
        val accountID = accountService.accountID()
        val bankKey = BANK_STORAGE_KEY_FORMAT.format(accountService.accountID())
        val characters = characterService.characters()
        val storageItems = characters.flatMap { storageRetrievalService.inventoryItems(it) }
            .toMutableList()
        storageItems.addAll(storageRetrievalService.bankItems())
        val materialItems = storageRetrievalService.materialItems()

        val updatedIds = storageItems.filter { it.detail.sellable }.map { it.detail.id }
            .toMutableSet()
        updatedIds.addAll(materialItems.filter { it.detail.sellable }.map { it.detail.id })
        val shouldUpdate = datastore.itemDAO.getAllSellableIds()
            .filterNot { i -> updatedIds.any { it == i } }
        val items = storageRetrievalService.fullItemDetails(shouldUpdate.toSet()).values
            .toMutableSet()
        items.addAll(storageItems.map { it.detail })
        items.addAll(materialItems.map { it.detail })

        val storageTypes = characters.toMutableList()
        storageTypes.add(bankKey)

        val lastModified = storageTypes.map { LastModified(it, System.currentTimeMillis()) }
            .toMutableList()
        lastModified.add(
            LastModified(
                MATERIAL_STORAGE_PREFIX.format(accountID), System.currentTimeMillis()
            )
        )

        datastore.withTransaction {
            datastore.storageDAO.bulkDelete(storageTypes)
            datastore.materialStorageDAO.bulkDelete(accountID)

            datastore.itemDAO.insertAll(items.map { it.toDAO() })
            datastore.storageDAO.insertAll(storageItems.map { it.toStorageDAO() })
            datastore.materialTypeDAO.insertAll(materialItems.map { it.category!!.toDAO() })
            datastore.materialStorageDAO.insertAll(materialItems.map { it.toMaterialDAO() })
            datastore.lastModifiedDAO.insertAll(lastModified)
        }

        Timber.d("Successfully updated ${items.size} item entries")
        Timber.d("Successfully updated ${storageItems.size} storage item entries")
        Timber.d("Successfully updated ${materialItems.size} material item entries")
    }

    override suspend fun updateStorage(storageType: String) {
        val items = if (storageType.contains(BANK_STORAGE_PREFIX)) {
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
        Timber.d("Successfully updated ${items.size} storage item entries")
    }

    override suspend fun updateMaterial() {
        val accountID = accountService.accountID()
        val items = storageRetrievalService.materialItems()

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
        return lastModified == null || lastModified.elapsedTime() < MIN_REFRESH_RATE_HR
    }
}