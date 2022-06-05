package me.xhsun.gw2leo.storage.datastore.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.xhsun.gw2leo.storage.datastore.entity.Storage
import me.xhsun.gw2leo.storage.datastore.entity.StorageBase

@Dao
interface StorageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<StorageBase>)

    @Query("DELETE FROM storage WHERE storageType = :storageType")
    fun bulkDelete(storageType: String)

    @Query("SELECT *, i.id as itemItemID FROM storage INNER JOIN item as i ON storage.itemID = i.id AND storage.storageType = :storageType ORDER BY i.buy ASC")
    fun getAllOderByBuyAscending(
        storageType: String
    ): PagingSource<Int, Storage>

    @Query("SELECT *, i.id as itemItemID FROM storage INNER JOIN item as i ON storage.itemID = i.id AND storage.storageType = :storageType ORDER BY i.buy DESC")
    fun getAllOderByBuyDescending(
        storageType: String
    ): PagingSource<Int, Storage>

    @Query("SELECT *, i.id as itemItemID FROM storage INNER JOIN item as i ON storage.itemID = i.id AND storage.storageType = :storageType ORDER BY i.sell ASC")
    fun getAllOderBySellAscending(
        storageType: String
    ): PagingSource<Int, Storage>

    @Query("SELECT *, i.id as itemItemID FROM storage INNER JOIN item as i ON storage.itemID = i.id AND storage.storageType = :storageType ORDER BY i.sell DESC")
    fun getAllOderBySellDescending(
        storageType: String
    ): PagingSource<Int, Storage>
}