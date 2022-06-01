package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import me.xhsun.gw2leo.account.datastore.entity.Account

@Entity(
    tableName = "materialstorage",
    primaryKeys = ["itemID", "accountID", "categoryID"],
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["accountID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MaterialType::class,
            parentColumns = ["id"],
            childColumns = ["categoryID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MaterialStorageBase(
    @ColumnInfo(index = true)
    val itemID: Int,
    @ColumnInfo(index = true)
    val accountID: String,
    @ColumnInfo(index = true)
    val categoryID: Int,
    @ColumnInfo(defaultValue = "false") val accountBounded: Boolean,
    @ColumnInfo(defaultValue = "0") val count: Int
)
