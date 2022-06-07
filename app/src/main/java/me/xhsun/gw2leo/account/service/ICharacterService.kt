package me.xhsun.gw2leo.account.service

import me.xhsun.gw2leo.account.datastore.entity.Character

interface ICharacterService {
    suspend fun characters(): List<String>

    /**
     * Sync character name list for the current account
     */
    fun sync(characters: List<Character>)
}