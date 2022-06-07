package me.xhsun.gw2leo.account.service

interface IAccountService {
    fun api(): String
    fun accountID(): String

    /**
     * Sync currently selected account to the provided one
     */
    suspend fun sync()
}