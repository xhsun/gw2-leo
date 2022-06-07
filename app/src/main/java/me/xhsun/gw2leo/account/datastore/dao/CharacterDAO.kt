package me.xhsun.gw2leo.account.datastore.dao

import androidx.room.*
import me.xhsun.gw2leo.account.datastore.entity.Character

@Dao
interface CharacterDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(characters: List<Character>)

    @Delete
    suspend fun bulkDelete(character: List<Character>)

    @Update
    suspend fun bulkUpdate(character: List<Character>)

    @Query("SELECT * FROM character")
    suspend fun getAll(): List<Character>

    @Query("SELECT * FROM character WHERE accountID = :accountID")
    suspend fun getAll(accountID: String): List<Character>
}