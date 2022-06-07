package me.xhsun.gw2leo.account.service

import me.xhsun.gw2leo.account.datastore.entity.Character
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.core.config.BANK_STORAGE_KEY_FORMAT
import me.xhsun.gw2leo.core.datastore.IDatastoreRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterService @Inject constructor(
    private val datastore: IDatastoreRepository,
    private val accountService: IAccountService
) : ICharacterService {

    @Volatile
    private var characters: List<String> = emptyList()

    override suspend fun characters(): List<String> {
        if (this.characters.isEmpty()) {
            val accountID = accountService.accountID()
            val bankKey = BANK_STORAGE_KEY_FORMAT.format(accountID)
            Timber.d("Account character list is empty, attempt to retrieve from cache")
            val characters = datastore.characterDAO.getAll(accountService.accountID()).map {
                it.name
            }.filter { it != bankKey }
            synchronized(this.characters) {
                if (characters.isNotEmpty()) {
                    this.characters = characters
                } else {
                    throw NotLoggedInError()
                }
            }
        }
        Timber.d("Account API key retrieved::${this.characters}")
        return this.characters
    }

    override fun sync(characters: List<Character>) {
        val accountID = accountService.accountID()
        val bankKey = BANK_STORAGE_KEY_FORMAT.format(accountID)
        Timber.d("Start update character list information::${accountID}")
        synchronized(this.characters) {
            this.characters = characters.map { it.name }.filter { it != bankKey }
        }
        Timber.d("Character list information updated::${this.characters}")
    }
}