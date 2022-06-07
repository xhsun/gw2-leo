package me.xhsun.gw2leo.storage.service

import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.config.BANK_STORAGE_KEY_FORMAT
import me.xhsun.gw2leo.config.ID_SEPARATOR
import me.xhsun.gw2leo.config.MAX_RESPONSE_SIZE
import me.xhsun.gw2leo.http.IGW2Repository
import me.xhsun.gw2leo.http.IGW2RepositoryFactory
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.error.NoItemFoundError
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class StorageRetrievalService @Inject constructor(
    gw2RepositoryFactory: IGW2RepositoryFactory,
    private val accountService: IAccountService
) : IStorageRetrievalService {
    private val gw2Repository: IGW2Repository = gw2RepositoryFactory.gw2Repository()

    override suspend fun bankItems(): List<StorageItem> {
        val accountID = accountService.accountID()
        Timber.d("Retrieving update bank storage items::$accountID")
        val response = gw2Repository.getBank()
        val res = response.mapNotNull {
            it?.toDomain(BANK_STORAGE_KEY_FORMAT.format(accountID))
        }.filter { it.count > 0 }
        if (res.isEmpty()) {
            Timber.d("Got empty bank storage item list")
            throw NoItemFoundError()
        }
        val items = this.fullItemDetails(res.map { it.detail.id }.toSet())
        return res.mapNotNull {
            val i = items[it.detail.id]
            if (i != null) {
                it.update(i, null)
            } else {
                null
            }
        }
    }

    override suspend fun inventoryItems(characterName: String): List<StorageItem> {
        Timber.d("Retrieving update inventory items::$characterName")
        val response = gw2Repository.getCharacterInventory(characterName)
        val res = response.bags.filterNotNull().flatMap { bag ->
            bag.inventory.mapNotNull {
                it?.toDomain(characterName)
            }
        }.filter { it.count > 0 }
        if (res.isEmpty()) {
            Timber.d("Got empty bank storage item list")
            throw NoItemFoundError()
        }
        val items = this.fullItemDetails(res.map { it.detail.id }.toSet())
        return res.mapNotNull {
            val i = items[it.detail.id]
            if (i != null) {
                it.update(i, null)
            } else {
                null
            }
        }
    }

    override suspend fun materialItems(): List<StorageItem> {
        val accountID = accountService.accountID()
        Timber.d("Start update material storage items")
        val response = gw2Repository.getMaterialBank()
        val res = response.map { it.toDomain(accountID) }.filter { it.count > 0 }
        if (res.isEmpty()) {
            Timber.d("Got empty material storage item list")
            throw NoItemFoundError()
        }
        val categories =
            gw2Repository.getMaterialBankInfo(res.map { it.category!!.id }
                .joinToString(separator = ID_SEPARATOR))
                .map {
                    it.toDomain()
                }.associateBy({ it.id }, { it })
        val details = this.fullItemDetails(res.map { it.detail.id }.toSet())
        return res.mapNotNull {
            val c = categories[it.category!!.id]
            val d = details[it.detail.id]
            if (c != null && d != null) {
                it.update(d, c)
            } else {
                null
            }
        }
    }


    override suspend fun fullItemDetails(ids: Set<Int>): Map<Int, Item> {
        Timber.d("Start retrieving item information:: ${ids.size}")
        var items = this.getItems(ids)
        if (items.isEmpty()) {
            throw NoItemFoundError()
        }

        val itemMap = items.associateBy({ it.id }, { it }).toMutableMap()

        items = this.getItemPrices(items)
        if (items.isNotEmpty()) {
            itemMap.putAll(items.associateBy({ it.id }, { it }))
        }
        return itemMap
    }

    /**
     * Get item information for the given ids
     * @param ids Item IDs
     * @return List of item information
     */
    private suspend fun getItems(ids: Set<Int>): List<Item> {
        if (ids.isEmpty()) {
            throw IllegalArgumentException("ids")
        }

        val idChunks = ids.chunked(MAX_RESPONSE_SIZE)
        val items = emptyList<Item>().toMutableList()
        try {
            idChunks.forEach {
                val id = it.joinToString(separator = ID_SEPARATOR)
                Timber.d("Start retrieving items::${id}")
                val t = gw2Repository.getItems(id)
                items.addAll(t.map { i ->
                    i.toDomain()
                })

            }
        } catch (e: Exception) {
            Timber.d("Encountered an error while retrieving items::${e.message}")
            when (e) {
                is HttpException -> {
                    if (e.code() == 400) {
                        Timber.d("Error is recoverable, return items::${items.size}")
                        return items
                    }
                }
            }
            throw e
        }
        Timber.d("Successfully retrieved items::${items.size}")
        return items
    }


    /**
     * Get item price information for the given items
     * @param shouldUpdate Items to be updated
     * @return List of updated items
     */
    private suspend fun getItemPrices(shouldUpdate: List<Item>): List<Item> {
        val itemChunks = shouldUpdate.filter { it.sellable }.chunked(MAX_RESPONSE_SIZE)
        val result = emptyList<Item>().toMutableList()

        try {
            itemChunks.forEach {
                val items = it.associateBy({ i -> i.id }, { i -> i })
                val id = items.keys.joinToString(separator = ID_SEPARATOR)
                Timber.d("Start retrieving item prices::${id}")
                result.addAll(gw2Repository.getPrices(id).mapNotNull { i ->
                    items[i.id]?.updatePrice(
                        i.buys.unitPrice,
                        i.sells.unitPrice
                    )
                })
            }
        } catch (e: Exception) {
            Timber.d("Encountered an error while retrieving item prices::${e.message}")
            when (e) {
                is HttpException -> {
                    if (e.code() == 400) {
                        Timber.d("Error is recoverable, return item prices::${result.size}")
                        return result
                    }
                }
            }
            throw e
        }
        Timber.d("Successfully retrieved item prices::${result.size}")
        return result
    }

}