package me.xhsun.gw2leo.account.datastore

interface IAccountIDRepository {
    /**
     * Get current Account ID from cache
     * @return Account ID
     */
    fun getCurrent(): String

    /**
     * Save current Account ID to cache, this method will override existing account ID in cache
     * @param accountID The ID that will be saved to cache
     * @return true if successful, false otherwise
     */
    fun updateCurrent(accountID: String): Boolean
}