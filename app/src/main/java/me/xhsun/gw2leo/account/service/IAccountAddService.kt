package me.xhsun.gw2leo.account.service

import me.xhsun.gw2leo.account.Account

interface IAccountAddService {
    fun add(API: String): Account
}