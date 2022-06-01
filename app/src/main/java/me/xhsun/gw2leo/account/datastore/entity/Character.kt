package me.xhsun.gw2leo.account.datastore.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Account::class,
        parentColumns = ["id"],
        childColumns = ["accountID"],
        onDelete = CASCADE
    )]
)
data class Character(
    @PrimaryKey val name: String,
    @ColumnInfo(index = true)
    val accountID: String
)
