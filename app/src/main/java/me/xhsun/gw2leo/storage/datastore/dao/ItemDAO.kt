package me.xhsun.gw2leo.storage.datastore.dao

import androidx.room.*
import me.xhsun.gw2leo.storage.datastore.entity.Item

@Dao
interface ItemDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Item>)

    @Update
    suspend fun bulkUpdate(items: List<Item>)

    @Delete
    suspend fun bulkDelete(vararg items: Item)

    @Query("SELECT id FROM item WHERE sellable = 1")
    suspend fun getAllSellableIds(): List<Int>
}