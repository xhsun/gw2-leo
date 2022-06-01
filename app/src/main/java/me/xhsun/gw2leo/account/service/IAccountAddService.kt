package me.xhsun.gw2leo.account.service

import me.xhsun.gw2leo.account.Account

interface IAccountAddService {
    suspend fun add(API: String): Account
}