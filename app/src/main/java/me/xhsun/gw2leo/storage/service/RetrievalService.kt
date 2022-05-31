package me.xhsun.gw2leo.storage.service

import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.config.DB_BANK_KEY_FORMAT
import me.xhsun.gw2leo.config.DEFAULT_RESPONSE_SIZE
import me.xhsun.gw2leo.config.ID_SEPARATOR
import me.xhsun.gw2leo.config.TOTAL_PAGE_HEADER
import me.xhsun.gw2leo.http.IGW2Repository
import me.xhsun.gw2leo.http.IGW2RepositoryFactory
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.MaterialCategory
import me.xhsun.gw2leo.storage.MaterialItem
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.error.HTTPError
import timber.log.Timber
import javax.inject.Inject

class RetrievalService @Inject constructor(
    gw2RepositoryFactory: IGW2RepositoryFactory,
    private val accountService: IAccountService
) : IRetrievalService {
    private val gw2Repository: IGW2Repository = gw2RepositoryFactory.gw2Repository()

    override fun getMaterialCategories(vararg ids: Int): List<MaterialCategory> {
        if (ids.isEmpty()) {
            throw IllegalArgumentException("ids")
        }

        val idString = ids.joinToString(separator = ID_SEPARATOR)
        Timber.d("Start retrieving material types::${idString}")
        val categories = gw2Repository.getMaterialBankInfo(idString).map {
            it.toDomain()
        }
        Timber.d("Successfully retrieved material categories::${categories.size}")
        return categories
    }

    override fun getItems(vararg ids: Int): List<Item> {
        if (ids.isEmpty()) {
            throw IllegalArgumentException("ids")
        }

        val idString = ids.joinToString(separator = ID_SEPARATOR)
        var currentPage = 0
        val pageSize = this.pageSize(ids.size)
        val items = emptyList<Item>().toMutableList()
        Timber.d("Start retrieving item prices::${pageSize}::${idString}")

        val r = this.getItem(idString, currentPage++, pageSize)
        val totalPages = r.first
        items.addAll(r.second)
        while (currentPage < totalPages) {
            items.addAll(this.getItem(idString, currentPage++, pageSize).second)
        }
        Timber.d("Successfully retrieved items::${items.size}")
        return items
    }

    override fun getItemPrices(vararg shouldUpdate: Item): List<Item> {
        if (shouldUpdate.isEmpty()) {
            Timber.d("Item IDs not provided")
            throw IllegalArgumentException("shouldUpdate")
        }
        val items = shouldUpdate.associateBy({ it.id }, { it })
        val idString = items.keys.joinToString(separator = ID_SEPARATOR)
        var currentPage = 0
        val pageSize = this.pageSize(items.size)
        val result = emptyList<Item>().toMutableList()
        Timber.d("Start retrieving item prices::${pageSize}::${items.size}")

        val r = this.getItemPrice(idString, currentPage++, pageSize, items)
        val totalPages = r.first
        result.addAll(r.second)
        while (currentPage < totalPages) {
            result.addAll(this.getItemPrice(idString, currentPage++, pageSize, items).second)
        }
        Timber.d("Successfully retrieved item prices::${result.size}")
        return result
    }

    override fun getMaterialItems(): List<MaterialItem> {
        val accountID = accountService.accountID()
        var currentPage = 0
        val items = emptyList<MaterialItem>().toMutableList()
        Timber.d("Start retrieving material items")

        val r = this.getMaterialItem(accountID, currentPage++)
        val totalPages = r.first
        items.addAll(r.second)
        while (currentPage < totalPages) {
            items.addAll(this.getMaterialItem(accountID, currentPage++).second)
        }
        val result = items.filter { it.count > 1 }
        Timber.d("Successfully retrieved material items::${result.size}")
        return result
    }

    override fun getBankItems(): List<StorageItem> {
        val accountID = accountService.accountID()
        var currentPage = 0
        val items = emptyList<StorageItem>().toMutableList()
        Timber.d("Start retrieving bank items")

        val r = this.getBankItem(accountID, currentPage++)
        val totalPages = r.first
        items.addAll(r.second)
        while (currentPage < totalPages) {
            items.addAll(this.getBankItem(accountID, currentPage++).second)
        }
        val result = items.filter { it.count > 1 }
        Timber.d("Successfully retrieved bank items::${result.size}")
        return result
    }

    override fun getInventoryItems(characterName: String): List<StorageItem> {
        if (characterName.isEmpty()) {
            Timber.d("Character name not provided")
            throw IllegalArgumentException("characterName")
        }
        var currentPage = 0
        val items = emptyList<StorageItem>().toMutableList()
        Timber.d("Start retrieving inventory items::${characterName}")

        val r = this.getInventoryItem(characterName, currentPage++)
        val totalPages = r.first
        items.addAll(r.second)
        while (currentPage < totalPages) {
            items.addAll(this.getInventoryItem(characterName, currentPage++).second)
        }
        val result = items.filter { it.count > 1 }
        Timber.d("Successfully retrieved inventory items::${characterName}::${result.size}")
        return result
    }

    /**
     * Get item information from current page
     * @param characterName List of ids as comma delimited list string
     * @param current Current page
     */
    private fun getInventoryItem(
        characterName: String,
        current: Int,
    ): Pair<Int, List<StorageItem>> {
        val response =
            gw2Repository.getCharacterInventory(characterName, current, DEFAULT_RESPONSE_SIZE)
        if (response.isSuccessful) {
            return Pair(
                response.headers()[TOTAL_PAGE_HEADER]?.toInt()
                    ?: throw HTTPError("Received invalid total page count"),
                response.body()?.flatMap { bag ->
                    bag.inventory.mapNotNull {
                        it?.toDomain(characterName)
                    }
                } ?: throw HTTPError("Received empty item list"))
        } else {
            throw HTTPError(response.errorBody().toString())
        }
    }

    /**
     * Get bank storage item information from current page
     * @param current Current page
     */
    private fun getBankItem(
        accountID: String,
        current: Int,
    ): Pair<Int, List<StorageItem>> {
        val response = gw2Repository.getBank(current, DEFAULT_RESPONSE_SIZE)
        if (response.isSuccessful) {
            return Pair(
                response.headers()[TOTAL_PAGE_HEADER]?.toInt()
                    ?: throw HTTPError("Received invalid total page count"),
                response.body()?.mapNotNull {
                    it?.toDomain(DB_BANK_KEY_FORMAT.format(accountID))
                } ?: throw HTTPError("Received empty item list"))
        } else {
            throw HTTPError(response.errorBody().toString())
        }
    }

    /**
     * Get material storage item information from current page
     * @param current Current page
     */
    private fun getMaterialItem(
        accountID: String,
        current: Int,
    ): Pair<Int, List<MaterialItem>> {
        val response = gw2Repository.getMaterialBank(current, DEFAULT_RESPONSE_SIZE)
        if (response.isSuccessful) {
            return Pair(
                response.headers()[TOTAL_PAGE_HEADER]?.toInt()
                    ?: throw HTTPError("Received invalid total page count"),
                response.body()?.map {
                    it.toDomain(accountID)
                } ?: throw HTTPError("Received empty item list"))
        } else {
            throw HTTPError(response.errorBody().toString())
        }
    }

    /**
     * Get item information from current page
     * @param idString List of ids as comma delimited list string
     * @param current Current page
     * @param totalPageSize Total page size
     */
    private fun getItem(
        idString: String,
        current: Int,
        totalPageSize: Int
    ): Pair<Int, List<Item>> {
        val response = gw2Repository.getItems(idString, current, totalPageSize)
        if (response.isSuccessful) {
            return Pair(
                response.headers()[TOTAL_PAGE_HEADER]?.toInt()
                    ?: throw HTTPError("Received invalid total page count"),
                response.body()?.map {
                    it.toDomain()
                } ?: throw HTTPError("Received empty item list"))
        } else {
            throw HTTPError(response.errorBody().toString())
        }
    }

    /**
     * Get item price information from current page
     * @param idString List of ids as comma delimited list string
     * @param current Current page
     * @param totalPageSize Total page size
     */
    private fun getItemPrice(
        idString: String,
        current: Int,
        totalPageSize: Int,
        items: Map<Int, Item>
    ): Pair<Int, List<Item>> {
        val response = gw2Repository.getPrices(idString, current, totalPageSize)
        if (response.isSuccessful) {
            return Pair(
                response.headers()[TOTAL_PAGE_HEADER]?.toInt()
                    ?: throw HTTPError("Received invalid total page count"),
                response.body()?.mapNotNull {
                    items[it.id]?.updatePrice(
                        it.buys.unitPrice,
                        it.sells.unitPrice
                    )
                } ?: throw HTTPError("Received empty item price list"))
        } else {
            throw HTTPError(response.errorBody().toString())
        }
    }
    
    /**
     * @return DEFAULT_RESPONSE_SIZE if array size is too big, otherwise, array size
     */
    private fun pageSize(arraySize: Int): Int {
        return if (arraySize > DEFAULT_RESPONSE_SIZE) DEFAULT_RESPONSE_SIZE else arraySize
    }
}