package me.xhsun.gw2leo.storage.datastore.dao

import androidx.room.*
import me.xhsun.gw2leo.storage.datastore.entity.Item

@Dao
interface ItemDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: Item)

    @Update
    fun bulkUpdate(items: List<Item>): Int

    @Delete
    fun bulkDelete(vararg items: Item)

    @Query("SELECT id FROM item WHERE sellable = 1")
    fun getAllSellableIds(): List<Int>
}