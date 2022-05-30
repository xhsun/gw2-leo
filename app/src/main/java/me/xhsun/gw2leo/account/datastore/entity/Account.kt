package me.xhsun.gw2leo.account.datastore.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Account(
    @PrimaryKey val id: String,
    val name: String,
    val API: String
)
