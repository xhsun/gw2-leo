package me.xhsun.gw2leo.storage.datastore.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.xhsun.gw2leo.storage.datastore.entity.LastModified

@Dao
interface LastModifiedDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lastModified: LastModified)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lastModified: List<LastModified>)

    @Query("SELECT * FROM lastmodified WHERE type = :type")
    suspend fun get(type: String): LastModified?
}