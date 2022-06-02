package me.xhsun.gw2leo.storage.datastore.dao

import androidx.paging.PagingSource
import androidx.room.*
import me.xhsun.gw2leo.storage.datastore.entity.Storage
import me.xhsun.gw2leo.storage.datastore.entity.StorageBase

@Dao
interface StorageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: StorageBase)

    @Query("DELETE FROM storage WHERE storageType = :storageType")
    fun bulkDelete(storageType: String)

    @Query("SELECT *, item.id as itemItemID FROM storage INNER JOIN item ON storage.itemID = item.id AND storage.storageType = :storageType ORDER BY :orderBy ASC")
    fun getAllAscending(
        storageType: String,
        orderBy: String
    ): PagingSource<Int, Storage>

    @Query("SELECT *, item.id as itemItemID FROM storage INNER JOIN item ON storage.itemID = item.id AND storage.storageType = :storageType ORDER BY :orderBy DESC")
    fun getAllDescending(
        storageType: String,
        orderBy: String
    ): PagingSource<Int, Storage>

    @Transaction
    fun bulkUpdate(storageType: String, vararg items: StorageBase) {
        bulkDelete(storageType)
        insertAll(*items)
    }
}