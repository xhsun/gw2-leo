package me.xhsun.gw2leo.account.service

import me.xhsun.gw2leo.account.datastore.IAccountIDRepository
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.core.datastore.IDatastoreRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountService @Inject constructor(
    private val datastore: IDatastoreRepository,
    private val accountIDRepository: IAccountIDRepository
) : IAccountService {

    @Volatile
    private var accountID: String = accountIDRepository.getCurrent()

    @Volatile
    private var api: String = ""

    @Volatile
    private var name: String = ""

    override fun api(): String {
        if (api.isEmpty()) {
            Timber.d("API is empty, no way to recovery, ask user to log in")
            throw NotLoggedInError()
        }
        return api
    }

    override fun accountID(): String {
        if (accountID.isEmpty()) {
            Timber.d("Account ID is empty, no way to recovery, ask user to log in")
            throw NotLoggedInError()
        }
        return accountID
    }

    override suspend fun sync() {
        val accountID = accountIDRepository.getCurrent()
        val account = datastore.accountDAO.getByID(accountID) ?: throw NotLoggedInError()
        synchronized(this) {
            this.accountID = accountID
            api = account.API
            name = account.name
        }
    }
}