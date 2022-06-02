package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import me.xhsun.gw2leo.account.datastore.entity.Account

@Entity(
    primaryKeys = ["id", "storageType"],
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["accountID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StorageRemoteKey(
    @ColumnInfo(index = true)
    val id: Int,
    val prevKey: Int?,
    val nextKey: Int?,
    @ColumnInfo(index = true)
    val accountID: String,
    @ColumnInfo(index = true)
    val storageType: String
)

