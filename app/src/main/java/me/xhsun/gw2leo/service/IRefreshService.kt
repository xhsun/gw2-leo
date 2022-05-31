package me.xhsun.gw2leo.service

interface IRefreshService {
    /**
     * Reload all information related to the provided account
     * @param API API token for the account
     */
    suspend fun refreshAccount(API: String): Boolean

    /**
     * Reload all information related to the currently selected account
     */
    suspend fun refreshAccount(): Boolean

    /**
     * Reload all character and character inventory information related to the currently selected account
     */
    suspend fun refreshCharacters(): Boolean
}