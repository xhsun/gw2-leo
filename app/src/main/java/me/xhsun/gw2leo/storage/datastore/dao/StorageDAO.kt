package me.xhsun.gw2leo.storage.datastore.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.xhsun.gw2leo.storage.datastore.entity.Storage
import me.xhsun.gw2leo.storage.datastore.entity.StorageBase

@Dao
interface StorageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: StorageBase)

    @Query("DELETE FROM storage WHERE storageType = :storageType")
    fun bulkDelete(storageType: String)

    @Transaction
    @Query("SELECT * FROM storage WHERE storageType = :storageType")
    fun getAll(storageType: String): LiveData<List<Storage>>

    @Transaction
    fun bulkUpdate(storageType: String, vararg items: StorageBase) {
        bulkDelete(storageType)
        insertAll(*items)
    }
}