package me.xhsun.gw2leo.account.service

interface ICharacterService {
    fun characters(): List<String>
    fun update(): Boolean
}