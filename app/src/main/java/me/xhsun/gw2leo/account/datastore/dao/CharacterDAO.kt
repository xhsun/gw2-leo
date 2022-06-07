package me.xhsun.gw2leo.account.datastore.dao

import androidx.room.*
import me.xhsun.gw2leo.account.datastore.entity.Character

@Dao
interface CharacterDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(characters: List<Character>)

    @Delete
    suspend fun bulkDelete(vararg character: Character)

    @Query("SELECT * FROM character WHERE accountID = :accountID")
    suspend fun getAll(accountID: String): List<Character>
}