package me.xhsun.gw2leo.storage.datastore.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.xhsun.gw2leo.storage.datastore.entity.Storage
import me.xhsun.gw2leo.storage.datastore.entity.StorageBase

@Dao
interface StorageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: StorageBase)

    @Update
    fun bulkUpdate(items: List<StorageBase>): Int

    @Delete
    fun bulkDelete(vararg items: StorageBase)

    @Transaction
    @Query("SELECT * FROM storage WHERE storageType = :storageType")
    fun getAll(storageType: String): LiveData<List<Storage>>
}