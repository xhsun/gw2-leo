package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import me.xhsun.gw2leo.account.datastore.entity.Character

@Entity(
    tableName = "storage",
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = ["name"],
            childColumns = ["storageType"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StorageBase(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val itemID: Int,
    val storageType: String,
    val count: Int,
    @ColumnInfo(defaultValue = "0") val charges: Int,
    @ColumnInfo(defaultValue = "None") val binding: String,
    val bindTo: String?
)
