package me.xhsun.gw2leo.http

import me.xhsun.gw2leo.account.http.model.AccountDTO
import me.xhsun.gw2leo.storage.http.model.*
import retrofit2.Response
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
     *
     * Note: X-Page-Total response header contains total page size
     * @param name Character name
     * @param page Current page
     * @param pageSize (Default 50) Size per page, maximum allowed 200
     */
    @GET("characters/{name}/inventory")
    suspend fun getCharacterInventory(
        @Path("name") name: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 50
    ): Response<List<BagDTO>>

    /**
     * Returns the items stored in a player's vault (not including material storage)
     *
     * If a slot is empty, it will return null. The amount of slots/bank tabs is implied by the length of the array
     *
     * Note: X-Page-Total response header contains total page size
     * @param page Current page
     * @param pageSize (Default 50) Size per page, maximum allowed 200
     */
    @GET("account/bank")
    suspend fun getBank(
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 50
    ): Response<List<InventoryDTO?>>

    /**
     * Returns the materials stored in a player's vault
     *
     * Every material will be returned, even if they have a count of 0
     *
     * Note: X-Page-Total response header contains total page size
     * @param page Current page
     * @param pageSize (Default 50) Size per page, maximum allowed 200
     */
    @GET("account/materials")
    suspend fun getMaterialBank(
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 50
    ): Response<List<MaterialDTO>>

    /**
     * Returns information about items that were discovered by players in the game
     *
     * Note: X-Page-Total response header contains total page size
     * @param ids (Comma Delimited List) Request an array of items for the specified ids
     * @param lang (Valid options: en, es, de, fr, and zh) Request localized information
     * @param page Current page
     * @param pageSize (Default 50) Size per page, maximum allowed 200
     */
    @GET("items")
    suspend fun getItems(
        @Query("ids") ids: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 50,
        @Query("lang") lang: String = "en"
    ): Response<List<ItemDTO>>

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
     *
     * Note: X-Page-Total response header contains total page size
     * @param ids (Comma Delimited List) Request an array of items for the specified ids
     * @param page Current page
     * @param pageSize (Default 50) Size per page, maximum allowed 200
     */
    @GET("commerce/prices")
    suspend fun getPrices(
        @Query("ids") ids: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 50
    ): Response<List<PriceDTO>>
}