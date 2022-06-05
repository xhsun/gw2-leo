package me.xhsun.gw2leo.account.service

interface ICharacterService {
    suspend fun characters(): List<String>

    /**
     * Update character name list for the current account
     */
    suspend fun update(): Boolean
}