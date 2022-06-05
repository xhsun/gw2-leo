package me.xhsun.gw2leo.http

import me.xhsun.gw2leo.account.http.model.AccountDTO
import me.xhsun.gw2leo.storage.http.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IGW2Repository {
    /**
     * Returns information about player accounts
     */
    @GET("account")
    suspend fun getAccount(): AccountDTO

    /**
     * Return an array of characters by name
     */
    @GET("characters")
    suspend fun getAllCharacterName(): List<String>

    /**
     * Returns information about the inventory of a character attached to a specific account
     * @param name Character name
     */
    @GET("characters/{name}/inventory")
    suspend fun getCharacterInventory(@Path("name") name: String): BagsDTO

    /**
     * Returns the items stored in a player's vault (not including material storage)
     *
     * If a slot is empty, it will return null. The amount of slots/bank tabs is implied by the length of the array
     */
    @GET("account/bank")
    suspend fun getBank(): List<InventoryDTO?>

    /**
     * Returns the materials stored in a player's vault
     *
     * Every material will be returned, even if they have a count of 0
     */
    @GET("account/materials")
    suspend fun getMaterialBank(): List<MaterialDTO>

    /**
     * Returns information about items that were discovered by players in the game
     * @param ids (Comma Delimited List) Request an array of items for the specified ids
     * @param lang (Valid options: en, es, de, fr, and zh) Request localized information
     */
    @GET("items")
    suspend fun getItems(
        @Query("ids") ids: String,
        @Query("lang") lang: String = "en"
    ): List<ItemDTO>

    /**
     * Returns information about the categories in material storage
     *
     * @param ids (Comma Delimited List) Request an array of categories for the specified ids
     * @param lang (Valid options: en, es, de, fr, and zh) Request localized information
     */
    @GET("materials")
    suspend fun getMaterialBankInfo(
        @Query("ids") ids: String,
        @Query("lang") lang: String = "en"
    ): List<MaterialTypeDTO>

    /**
     * Returns current aggregated buy and sell listing information from the trading post
     * @param ids (Comma Delimited List) Request an array of items for the specified ids
     */
    @GET("commerce/prices")
    suspend fun getPrices(
        @Query("ids") ids: String,
    ): List<PriceDTO>
}