package me.xhsun.gw2leo.storage.datastore.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.xhsun.gw2leo.storage.datastore.entity.StorageRemoteKey

@Dao
interface StorageRemoteKeyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<StorageRemoteKey>)

    @Query("SELECT * FROM storageremotekey WHERE id = :id")
    suspend fun getByID(id: Int): StorageRemoteKey?

    @Query("DELETE FROM storageremotekey WHERE accountID = :accountID AND storageType = :storageType")
    suspend fun bulkDelete(accountID: String, storageType: String)
}