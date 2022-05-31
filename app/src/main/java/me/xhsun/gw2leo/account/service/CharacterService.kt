package me.xhsun.gw2leo.account.service

import me.xhsun.gw2leo.account.datastore.entity.Character
import me.xhsun.gw2leo.config.DB_BANK_KEY_FORMAT
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.http.IGW2Repository
import me.xhsun.gw2leo.http.IGW2RepositoryFactory
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterService @Inject constructor(
    private val datastore: IDatastoreRepository,
    gw2RepositoryFactory: IGW2RepositoryFactory,
    private val accountService: IAccountService
) : ICharacterService {
    private val gw2Repository: IGW2Repository = gw2RepositoryFactory.gw2Repository()

    @Volatile
    private var characters: List<String> = emptyList()

    override fun characters(): List<String> {
        if (this.characters.isEmpty()) {
            Timber.d("Account character list is empty, attempt to retrieve from cache")
            synchronized(this.characters) {
                val characters = datastore.characterDAO.getAll(accountService.accountID()).map {
                    it.name
                }
                if (characters.isNotEmpty()) {
                    this.characters = characters
                } else {
                    update()
                }
            }
        }
        Timber.d("Account API key retrieved::${this.characters}")
        return this.characters
    }

    override fun update(): Boolean {
        val accountID = accountService.accountID()
        Timber.d("Start update character list information::${accountID}")
        val characters = gw2Repository.getAllCharacterName().toMutableList()
        characters.add(DB_BANK_KEY_FORMAT.format(accountID))
        val characterArr = characters.map {
            Character(
                name = it,
                accountID = accountID
            )
        }.toTypedArray()
        val result: Boolean
        synchronized(this.characters) {
            this.characters = characters.toList()
            result = datastore.characterDAO.insertAll(*characterArr) == characters.size
        }
        Timber.d("Character list information updated::${result}::${this.characters}")
        return result
    }
}