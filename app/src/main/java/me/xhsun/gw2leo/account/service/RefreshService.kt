package me.xhsun.gw2leo.account.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.storage.service.StorageService
import timber.log.Timber
import javax.inject.Inject

class RefreshService @Inject constructor(
    private val accountService: IAccountService,
    private val characterService: ICharacterService,
    private val storageService: StorageService
) : IRefreshService {

    override suspend fun refreshAccount(API: String): Boolean {
        try {
            withContext(Dispatchers.IO) {
                if (accountService.update(API)) {
                    characterService.update()
                    storageService.updateAll()
                }
            }
            return true
        } catch (e: Exception) {
            when (e) {
                is NotLoggedInError -> throw e
                else -> {
                    Timber.d("Failed to update all storages::${e.message}")
                }
            }
        }
        return false
    }

    override suspend fun refreshAccount(): Boolean {
        try {
            withContext(Dispatchers.IO) {
                if (characterService.update()) {
                    storageService.updateAll()
                }
            }
            return true
        } catch (e: Exception) {
            when (e) {
                is NotLoggedInError -> throw e
                else -> {
                    Timber.d("Failed to update all storages::${e.message}")
                }
            }
        }
        return false
    }

    override suspend fun refreshCharacters(): Boolean {
        try {
            withContext(Dispatchers.IO) {
                if (characterService.update()) {
                    characterService.characters().forEach {
                        storageService.updateStorage(it)
                    }
                }
            }
            return true
        } catch (e: Exception) {
            when (e) {
                is NotLoggedInError -> throw e
                else -> {
                    Timber.d("Failed to update all character storages::${e.message}")
                }
            }
        }
        return false
    }
}