package me.xhsun.gw2leo.account.service

interface IAccountService {
    fun api(): String
    fun accountID(): String

    /**
     * Update currently selected account to the provided one
     * and update cache/datastore
     * @param API API token of the new account
     */
    fun update(API: String): Boolean
}