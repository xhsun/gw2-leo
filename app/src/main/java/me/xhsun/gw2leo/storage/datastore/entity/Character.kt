package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Account::class,
        parentColumns = ["API"],
        childColumns = ["API"],
        onDelete = CASCADE
    )]
)
data class Character(
    @PrimaryKey val name: String,
    val API: String
)
