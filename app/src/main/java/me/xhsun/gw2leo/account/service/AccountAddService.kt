package me.xhsun.gw2leo.account.service

import me.xhsun.gw2leo.account.Account
import me.xhsun.gw2leo.account.datastore.IAccountIDRepository
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.http.IGW2Repository
import me.xhsun.gw2leo.http.IGW2RepositoryFactory
import timber.log.Timber
import javax.inject.Inject

class AccountAddService @Inject constructor(
    gw2RepositoryFactory: IGW2RepositoryFactory,
    private val datastore: IDatastoreRepository,
    private val accountIDRepository: IAccountIDRepository,
    private val accountService: IAccountService
) : IAccountAddService {
    private val gw2Repository: IGW2Repository = gw2RepositoryFactory.gw2Repository()

    override suspend fun add(API: String): Account {
        accountService.updateAPI(API)
        Timber.d("Start update account information::${API}")
        val account = gw2Repository.getAccount().toDomain(API)
        Timber.d("Account information updated, start update cache::${account}")
        accountIDRepository.updateCurrent(account.id)
        datastore.accountDAO.insertAll(account.toDAO())

        Timber.d("Account information cache updated")
        accountService.update(account.id)
        return account
    }
}