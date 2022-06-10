package me.xhsun.gw2leo.core.refresh.service

interface IAccountRefreshService {
    /**
     * Initialize account and character information for the given API token
     * @param api OAuth API token
     */
    suspend fun initialize(api: String)

    /**
     * Update and sync character information for all registered account
     */
    suspend fun update()
}