package me.xhsun.gw2leo.account

data class Account(
    val id: String,
    val name: String,
    val api: String
) {
    fun toDAO(): me.xhsun.gw2leo.account.datastore.entity.Account {
        return me.xhsun.gw2leo.account.datastore.entity.Account(
            id = id,
            name = name,
            api = api
        )
    }
}
