package me.xhsun.gw2leo.account.service

import me.xhsun.gw2leo.account.Account
import me.xhsun.gw2leo.account.datastore.IAccountIDRepository
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.http.IGW2Repository
import me.xhsun.gw2leo.http.IGW2RepositoryFactory
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountService @Inject constructor(
    private val datastore: IDatastoreRepository,
    private val accountIDRepository: IAccountIDRepository,
    gw2RepositoryFactory: IGW2RepositoryFactory
) : IAccountService {
    private val gw2Repository: IGW2Repository = gw2RepositoryFactory.gw2Repository()

    @Volatile
    private var accountID: String = accountIDRepository.getCurrent()

    @Volatile
    private var api: String = ""

    @Volatile
    private var name: String = ""

    override fun api(): String {
        return api.ifEmpty {
            Timber.d("Account API key is empty, attempt to retrieve from cache")
            if (accountID.isEmpty()) {
                Timber.d("Account ID is empty, no way to recovery, ask user to log in")
                throw NotLoggedInError()
            }
            val account =
                datastore.accountDAO.getByID(accountID) ?: throw NotLoggedInError()
            synchronized(this.api) {
                api = account.API
            }
            synchronized(this.name) {
                name = account.name
            }
            Timber.d("Account API key retrieved::$api")
            api
        }
    }

    override fun accountID(): String {
        if (accountID.isEmpty()) {
            Timber.d("Account ID is empty, no way to recovery, ask user to log in")
            throw NotLoggedInError()
        }
        return accountID
    }

    override fun update(API: String): Boolean {
        Timber.d("Start update account information::${API}")
        var account: Account
        var cacheResult: Boolean
        synchronized(this.api) {
            api = API
            account = gw2Repository.getAccount().toDomain(api)
        }
        Timber.d("Account information updated, start update cache::${account}")
        synchronized(this.name) {
            name = account.name
        }
        synchronized(this.accountID) {
            accountID = account.id
            cacheResult = accountIDRepository.updateCurrent(accountID)
        }
        val count = datastore.accountDAO.insertAll(account.toDAO())
        val result = cacheResult && count > 1
        Timber.d("Account information cache updated::${result}")
        return result
    }
}