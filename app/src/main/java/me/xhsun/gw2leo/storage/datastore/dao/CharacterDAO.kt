package me.xhsun.gw2leo.storage.datastore.dao

import androidx.room.*
import me.xhsun.gw2leo.storage.datastore.entity.Character

@Dao
interface CharacterDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg characters: Character)

    @Delete
    fun bulkDelete(vararg character: Character)

    @Query("SELECT * FROM character WHERE accountID = :accountID")
    fun getAll(accountID: String): List<Character>
}