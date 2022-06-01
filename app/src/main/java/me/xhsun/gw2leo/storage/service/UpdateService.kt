package me.xhsun.gw2leo.storage.service

import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.config.DB_BANK_KEY_FORMAT
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import timber.log.Timber
import javax.inject.Inject

class UpdateService @Inject constructor(
    private val datastore: IDatastoreRepository,
    private val retrievalService: IRetrievalService,
    private val accountService: IAccountService
) : IUpdateService {

    override suspend fun addItems(vararg ids: Int): Boolean {
        if (ids.isEmpty()) {
            Timber.d("item IDs not provided")
            throw IllegalArgumentException("ids")
        }

        Timber.d("Start retrieving item information:: ${ids.size}")
        var items = retrievalService.getItems(*ids)
        if (items.isEmpty()) {
            Timber.d("Got empty item list")
            return false
        }

        items = retrievalService.getItemPrices(*items.toTypedArray())
        if (items.isNotEmpty()) {
            val temp = items.map { it.toDAO() }.toTypedArray()
            return try {
                datastore.itemDAO.insertAll(*temp)
                Timber.d("Successfully updated items::${items}")
                true
            } catch (e: Exception) {
                Timber.d("Failed to updated items:: ${e.message}")
                false
            }
        }
        Timber.d("Failed to updated items")
        return false
    }

    override suspend fun updateItems(): Boolean {
        val sellable = datastore.itemDAO.getAllSellableIds()
        if (sellable.isEmpty()) {
            Timber.d("Datastore is empty")
            throw IllegalArgumentException("ids")
        }
        val itemIds = sellable.toIntArray()

        Timber.d("Start update item information:: ${itemIds.size}")
        var items = retrievalService.getItems(*itemIds)
        if (items.isEmpty()) {
            Timber.d("Got empty item list")
            return false
        }

        items = retrievalService.getItemPrices(*items.toTypedArray())
        if (items.isNotEmpty()) {
            datastore.itemDAO.bulkUpdate(items.map {
                it.toDAO()
            })
            Timber.d("Successfully updated items::${items}")
            return true
        }
        Timber.d("Failed to updated items")
        return false
    }

    override suspend fun updateMaterialCategories(vararg ids: Int): Boolean {
        var typeIds = ids
        if (typeIds.isEmpty()) {
            Timber.d("Material type IDs not provided, start updating existing material types")
            typeIds = datastore.materialTypeDAO.getAllIds().toIntArray()
        }

        val categories = retrievalService.getMaterialCategories(*typeIds).map {
            it.toDAO()
        }.toTypedArray()
        if (categories.isEmpty()) {
            Timber.d("Got empty material categories list")
            return false
        }

        datastore.materialTypeDAO.insertAll(*categories)
        Timber.d("Successfully updated material types::${categories}")
        return true
    }

    override suspend fun updateMaterialItems(): Boolean {
        val accountID = accountService.accountID()
        Timber.d("Start update material storage items")
        val items = retrievalService.getMaterialItems()
        if (items.isEmpty()) {
            Timber.d("Got empty material storage item list")
        }
        val typeIds = items.map { it.category.id }.toIntArray()
        this.updateMaterialCategories(*typeIds)
        val itemIds = items.map { it.detail.id }.toIntArray()
        this.addItems(*itemIds)

        val result = items.map { it.toDAO() }.toTypedArray()
        return try {
            datastore.materialStorageDAO.bulkUpdate(accountID, *result)
            true
        } catch (e: Exception) {
            Timber.d("Failed to updated items:: ${e.message}")
            false
        }
    }

    override suspend fun updateBankItems(): Boolean {
        val accountID = accountService.accountID()
        Timber.d("Start update bank storage items")
        val items = retrievalService.getBankItems()
        if (items.isEmpty()) {
            Timber.d("Got empty bank storage item list")
        }
        val itemIds = items.map { it.detail.id }.toIntArray()
        this.addItems(*itemIds)

        val result = items.map { it.toDAO() }.toTypedArray()
        return try {
            datastore.storageDAO.bulkUpdate(DB_BANK_KEY_FORMAT.format(accountID), *result)
            true
        } catch (e: Exception) {
            Timber.d("Failed to updated items:: ${e.message}")
            false
        }
    }

    override suspend fun updateInventoryItems(characterName: String): Boolean {
        if (characterName.isEmpty()) {
            Timber.d("character name is empty")
            throw IllegalArgumentException("characterName")
        }
        Timber.d("Start update character storage items:: $characterName")
        val items = retrievalService.getInventoryItems(characterName)
        if (items.isEmpty()) {
            Timber.d("Got empty character storage item list:: $characterName")
        }
        val itemIds = items.map { it.detail.id }.toIntArray()
        this.addItems(*itemIds)

        val result = items.map { it.toDAO() }.toTypedArray()
        return try {
            datastore.storageDAO.bulkUpdate(characterName, *result)
            true
        } catch (e: Exception) {
            Timber.d("Failed to updated items:: ${e.message}")
            false
        }
    }
}