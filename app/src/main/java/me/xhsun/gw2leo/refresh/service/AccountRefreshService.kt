package me.xhsun.gw2leo.refresh.service

import androidx.room.withTransaction
import me.xhsun.gw2leo.account.datastore.IAccountIDRepository
import me.xhsun.gw2leo.account.datastore.entity.Character
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.service.ICharacterService
import me.xhsun.gw2leo.config.AUTH_BODY_FORMAT
import me.xhsun.gw2leo.config.BANK_STORAGE_KEY_FORMAT
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.http.IGW2Repository
import me.xhsun.gw2leo.http.IGW2RepositoryFactory
import timber.log.Timber
import javax.inject.Inject


class AccountRefreshService @Inject constructor(
    gw2RepositoryFactory: IGW2RepositoryFactory,
    private val datastore: IDatastoreRepository,
    private val accountIDRepository: IAccountIDRepository,
    private val accountService: IAccountService,
    private val characterService: ICharacterService
) : IAccountRefreshService {
    private val gw2Repository: IGW2Repository = gw2RepositoryFactory.gw2Repository()

    override suspend fun initialize(API: String) {
        Timber.d("Start initializing account information::${API}")
        val account = gw2Repository.getAccount(AUTH_BODY_FORMAT.format(API)).toDomain(API)
        val characters = this.getCharacters(API, account.id)

        Timber.d("Account information retrieved, start initializing cache::${account}::$characters")
        datastore.withTransaction {
            datastore.accountDAO.insertAll(account.toDAO())
            datastore.characterDAO.insertAll(characters)
        }
        accountIDRepository.updateCurrent(account.id)

        accountService.sync()
        characterService.sync(characters)
        Timber.d("Account information initialized")
    }

    override suspend fun update() {
        Timber.d("Start updating all character information")
        val current = accountService.accountID()
        var currentCharacters = emptyList<Character>()

        val accounts = datastore.accountDAO.getAll()
        val oldCharacters = datastore.characterDAO.getAll()
        val newCharacters = accounts.flatMap {
            val c = getCharacters(it.API, it.id)
            if (it.id == current) {
                currentCharacters = c
            }
            c
        }

        val shouldDelete = oldCharacters.filterNot { c -> newCharacters.any { it.name == c.name } }
        val shouldInsert = newCharacters.filterNot { c -> oldCharacters.any { it.name == c.name } }
        val shouldUpdate = newCharacters.filter { c -> oldCharacters.any { it.name == c.name } }
        datastore.withTransaction {
            if (shouldDelete.isNotEmpty()) {
                Timber.d("Removing following character::$shouldDelete")
                datastore.characterDAO.bulkDelete(shouldDelete)
            }
            if (shouldUpdate.isNotEmpty()) {
                Timber.d("Updating following character::$shouldUpdate")
                datastore.characterDAO.bulkUpdate(shouldUpdate)
            }
            if (shouldInsert.isNotEmpty()) {
                Timber.d("Adding following character::$shouldInsert")
                datastore.characterDAO.insertAll(shouldInsert)
            }
        }

        if (currentCharacters.isNotEmpty()) {
            Timber.d("Updating current character information::$currentCharacters")
            characterService.sync(currentCharacters)
        }
        Timber.d("All character information updated")
    }

    /**
     * Get list of characters from endpoint and add bank key into the list
     */
    private suspend fun getCharacters(API: String, accountID: String): List<Character> {
        val characters = gw2Repository.getAllCharacterName(AUTH_BODY_FORMAT.format(API)).map {
            Character(it, accountID)
        }.toMutableList()
        characters.add(Character(BANK_STORAGE_KEY_FORMAT.format(accountID), accountID))
        return characters
    }
}