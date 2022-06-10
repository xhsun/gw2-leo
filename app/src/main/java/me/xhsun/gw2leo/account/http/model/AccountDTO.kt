package me.xhsun.gw2leo.account.http.model

import me.xhsun.gw2leo.account.Account

data class AccountDTO(
    val id: String,
    val name: String
) {
    fun toDomain(api: String): Account {
        return Account(
            id = id,
            name = name,
            api = api
        )
    }
}
