package me.xhsun.gw2leo.account.service

interface IRefreshService {
    /**
     * Reload account and character information related to the provided account
     * @param API API token for the account
     */
    suspend fun initializeAccount(API: String): Boolean

    /**
     * Reload all information related to the currently selected account
     */
    suspend fun refreshAccount(): Boolean
}