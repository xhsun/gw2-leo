package me.xhsun.gw2leo.core.refresh.service

interface IAccountRefreshService {
    /**
     * Initialize account and character information for the given API token
     * @param API OAuth API token
     */
    suspend fun initialize(API: String)

    /**
     * Update and sync character information for all registered account
     */
    suspend fun update()
}