package me.xhsun.gw2leo.storage.datastore.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorageBase

@Dao
interface MaterialStorageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: MaterialStorageBase)

    @Query("DELETE FROM materialstorage WHERE accountID = :accountID")
    fun bulkDelete(accountID: String)

    @Transaction
    @Query("SELECT * FROM materialstorage WHERE accountID = :accountID")
    fun getAll(accountID: String): LiveData<List<MaterialStorage>>

    @Transaction
    fun bulkUpdate(accountID: String, vararg items: MaterialStorageBase) {
        bulkDelete(accountID)
        insertAll(*items)
    }
}