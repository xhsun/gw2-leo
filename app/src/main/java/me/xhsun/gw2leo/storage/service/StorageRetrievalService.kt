package me.xhsun.gw2leo.storage.service

import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.config.*
import me.xhsun.gw2leo.http.IGW2Repository
import me.xhsun.gw2leo.http.IGW2RepositoryFactory
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.MaterialItem
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.error.HTTPError
import me.xhsun.gw2leo.storage.error.NoItemFoundError
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class StorageRetrievalService @Inject constructor(
    gw2RepositoryFactory: IGW2RepositoryFactory,
    private val accountService: IAccountService
) : IStorageRetrievalService {
    private val gw2Repository: IGW2Repository = gw2RepositoryFactory.gw2Repository()

    override suspend fun bankItems(current: Int, pageSize: Int): Pair<Boolean, List<StorageItem>> {
        val accountID = accountService.accountID()
        Timber.d("Retrieving update bank storage items::${current}:: $pageSize")
        val response = this.parseResponse(
            current,
            gw2Repository.getBank(current, pageSize)
        )
        val res = response.second.mapNotNull {
            it?.toDomain(DB_BANK_KEY_FORMAT.format(accountID))
        }.filter { it.count > 1 }
        if (res.isEmpty()) {
            Timber.d("Got empty bank storage item list")
            throw NoItemFoundError()
        }
        val items = res.associateBy({ it.id }, { it })
        val details = this.fullItemDetails(*items.keys.toIntArray())
        return Pair(response.first, details.mapNotNull {
            items[it.id]?.updateItem(it)
        })
    }

    override suspend fun inventoryItems(
        characterName: String,
        current: Int,
        pageSize: Int
    ): Pair<Boolean, List<StorageItem>> {
        Timber.d("Retrieving update inventory items::$characterName::${current}:: $pageSize")
        val response = this.parseResponse(
            current,
            gw2Repository.getCharacterInventory(characterName, current, pageSize)
        )
        val res = response.second.flatMap { bag ->
            bag.inventory.mapNotNull {
                it?.toDomain(characterName)
            }
        }.filter { it.count > 1 }
        if (res.isEmpty()) {
            Timber.d("Got empty bank storage item list")
            throw NoItemFoundError()
        }
        val items = res.associateBy({ it.id }, { it })
        val details = this.fullItemDetails(*items.keys.toIntArray())
        return Pair(response.first, details.mapNotNull {
            items[it.id]?.updateItem(it)
        })
    }

    override suspend fun materialItems(
        current: Int,
        pageSize: Int
    ): Pair<Boolean, List<MaterialItem>> {
        val accountID = accountService.accountID()
        Timber.d("Start update material storage items")
        val response = this.parseResponse(current, gw2Repository.getMaterialBank(current, pageSize))
        val res = response.second.map { it.toDomain(accountID) }.filter { it.count > 1 }
        if (res.isEmpty()) {
            Timber.d("Got empty material storage item list")
            throw NoItemFoundError()
        }
        val typeIds = res.map { it.category.id }.toIntArray()
        val categories =
            gw2Repository.getMaterialBankInfo(typeIds.joinToString(separator = ID_SEPARATOR))
                .map {
                    it.toDomain()
                }.associateBy({ it.id }, { it })
        val itemIds = res.map { it.detail.id }.toIntArray()
        val details = this.fullItemDetails(*itemIds).associateBy({ it.id }, { it })
        return Pair(response.first, res.mapNotNull {
            val c = categories[it.category.id]
            val d = details[it.detail.id]
            if (c != null && d != null) {
                it.update(d, c)
            }
            null
        })
    }


    override suspend fun fullItemDetails(vararg ids: Int): List<Item> {
        Timber.d("Start retrieving item information:: ${ids.size}")
        var items = this.getItems(*ids)
        if (items.isEmpty()) {
            Timber.d("Got empty item list")
            throw NoItemFoundError()
        }

        items = this.getItemPrices(*items.toTypedArray())
        if (items.isNotEmpty()) {
            return items
        }
        throw NoItemFoundError()
    }

    /**
     * Parse response object
     * @return first: true if currently at last page, second: response body
     * @exception HttpException If response not successful
     * @exception HTTPError If body is empty
     */
    private fun <T> parseResponse(current: Int, response: Response<T>): Pair<Boolean, T> {
        if (response.isSuccessful) {
            val total = response.headers()[TOTAL_PAGE_HEADER]?.toInt()
                ?: throw HTTPError(response.code(), "Invalid total page count")
            val res = response.body() ?: throw NoItemFoundError()
            return Pair(current >= (total - 1), res)
        }
        throw HTTPError(response.code(), response.errorBody().toString())
    }

    /**
     * Get item information for the given ids
     * @param ids Item IDs
     * @return List of item information
     */
    private suspend fun getItems(vararg ids: Int): List<Item> {
        if (ids.isEmpty()) {
            throw IllegalArgumentException("ids")
        }

        val idString = ids.joinToString(separator = ID_SEPARATOR)
        var currentPage = STARTING_PAGE_INDEX
        val items = emptyList<Item>().toMutableList()
        Timber.d("Start retrieving item prices::${idString}")

        val r = this.getItem(idString, currentPage++, MAX_RESPONSE_SIZE)
        items.addAll(r)
        try {
            while (true) {
                items.addAll(this.getItem(idString, currentPage++, MAX_RESPONSE_SIZE))
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    if (e.code() == 400) {
                        Timber.d("Successfully retrieved items::${items.size}")
                        return items
                    }
                }
            }
            throw e
        }
    }

    /**
     * Get item information from current page
     * @param idString List of ids as comma delimited list string
     * @param current Current page
     * @param totalPageSize Total page size
     */
    private suspend fun getItem(
        idString: String,
        current: Int,
        totalPageSize: Int
    ): List<Item> {
        val response = gw2Repository.getItems(idString, current, totalPageSize)
        return response.map {
            it.toDomain()
        }
    }

    /**
     * Get item price information for the given items
     * @param shouldUpdate Items to be updated
     * @return List of updated items
     */
    private suspend fun getItemPrices(vararg shouldUpdate: Item): List<Item> {
        val items = shouldUpdate.associateBy({ it.id }, { it })
        val idString = items.keys.joinToString(separator = ID_SEPARATOR)
        var currentPage = 0
        val result = emptyList<Item>().toMutableList()
        Timber.d("Start retrieving item prices::${items.size}")

        val r = this.getItemPrice(idString, currentPage++, MAX_RESPONSE_SIZE, items)
        result.addAll(r)
        try {
            while (true) {
                result.addAll(
                    this.getItemPrice(
                        idString,
                        currentPage++,
                        MAX_RESPONSE_SIZE,
                        items
                    )
                )
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    if (e.code() == 400) {
                        Timber.d("Successfully retrieved item prices::${items.size}")
                        return result
                    }
                }
            }
            throw e
        }
    }

    /**
     * Get item price information from current page
     * @param idString List of ids as comma delimited list string
     * @param current Current page
     * @param totalPageSize Total page size
     */
    private suspend fun getItemPrice(
        idString: String,
        current: Int,
        totalPageSize: Int,
        items: Map<Int, Item>
    ): List<Item> {
        val response = gw2Repository.getPrices(idString, current, totalPageSize)
        return response.mapNotNull {
            items[it.id]?.updatePrice(
                it.buys.unitPrice,
                it.sells.unitPrice
            )
        }
    }
}