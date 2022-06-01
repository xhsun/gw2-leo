package me.xhsun.gw2leo.account.service

interface IAccountService {
    fun api(): String
    fun accountID(): String

    /**
     * Update just the account API
     *
     * ONLY use this when loading new account information
     */
    fun updateAPI(api: String)

    /**
     * Update currently selected account to the provided one
     * and update cache/datastore
     * @param accountID account ID of the new account
     */
    fun update(accountID: String)
}