package me.xhsun.gw2leo.storage.datastore.dao

import androidx.room.*
import me.xhsun.gw2leo.storage.datastore.entity.MaterialType

@Dao
interface MaterialTypeDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg types: MaterialType)

    @Delete
    fun bulkDelete(vararg types: MaterialType)

    @Query("SELECT * FROM materialtype")
    fun getAll(): List<MaterialType>
}