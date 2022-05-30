package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["itemID", "API", "categoryID"],
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["API"],
            childColumns = ["API"],
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
data class MaterialStorage(
    val itemID: Int,
    val API: String,
    val categoryID: Int,
    @ColumnInfo(defaultValue = "false") val accountBounded: Boolean,
    @ColumnInfo(defaultValue = "0") val count: Int
)
