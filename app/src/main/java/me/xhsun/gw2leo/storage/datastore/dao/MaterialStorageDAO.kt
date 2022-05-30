package me.xhsun.gw2leo.storage.datastore.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorageBase

@Dao
interface MaterialStorageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: MaterialStorageBase)

    @Update
    fun bulkUpdate(items: List<MaterialStorageBase>): Int

    @Delete
    fun bulkDelete(vararg items: MaterialStorageBase)

    @Transaction
    @Query("SELECT * FROM materialstorage WHERE accountID = :accountID")
    fun getAll(accountID: String): LiveData<List<MaterialStorage>>
}