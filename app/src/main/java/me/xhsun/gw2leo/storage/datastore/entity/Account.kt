package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Account(
    @PrimaryKey val API: String,
)
