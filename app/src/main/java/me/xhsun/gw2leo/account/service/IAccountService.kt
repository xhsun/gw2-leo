package me.xhsun.gw2leo.account.service

interface IAccountService {
    fun api(): String
    fun accountID(): String
    fun update(API: String): Boolean
}