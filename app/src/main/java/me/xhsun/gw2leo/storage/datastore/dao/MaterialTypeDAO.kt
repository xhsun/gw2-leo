package me.xhsun.gw2leo.storage.datastore.dao

import androidx.room.*
import me.xhsun.gw2leo.storage.datastore.entity.MaterialType

@Dao
interface MaterialTypeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(types: List<MaterialType>)

    @Delete
    fun bulkDelete(vararg types: MaterialType)

    @Query("SELECT id FROM materialtype")
    fun getAllIds(): List<Int>
}