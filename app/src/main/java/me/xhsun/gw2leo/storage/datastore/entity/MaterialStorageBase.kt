package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.*
import me.xhsun.gw2leo.account.datastore.entity.Account

@Entity(
    tableName = "materialstorage",
    indices = [Index(value = ["itemID", "accountID", "categoryID"], unique = true)],
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
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(index = true)
    val itemID: Int,
    @ColumnInfo(index = true)
    val accountID: String,
    @ColumnInfo(index = true)
    val categoryID: Int,
    @ColumnInfo(defaultValue = "") val binding: String,
    @ColumnInfo(defaultValue = "0") val count: Int
)
