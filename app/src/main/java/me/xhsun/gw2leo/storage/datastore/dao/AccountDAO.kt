package me.xhsun.gw2leo.storage.datastore.dao

import androidx.room.*
import me.xhsun.gw2leo.storage.datastore.entity.Account

@Dao
interface AccountDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg accounts: Account)

    @Delete
    fun delete(account: Account)

    @Query("SELECT * FROM account")
    fun getAll(): List<Account>
}