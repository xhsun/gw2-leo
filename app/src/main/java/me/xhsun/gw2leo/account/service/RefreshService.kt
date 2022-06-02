package me.xhsun.gw2leo.account.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.storage.service.StorageService
import timber.log.Timber
import javax.inject.Inject

class RefreshService @Inject constructor(
    private val characterService: ICharacterService,
    private val storageService: StorageService,
    private val addService: IAccountAddService
) : IRefreshService {

    override suspend fun initializeAccount(API: String): Boolean {
        try {
            withContext(Dispatchers.IO) {
                addService.add(API)
                characterService.update()
            }
            return true
        } catch (e: Exception) {
            Timber.d("Failed to initialize account::${API}::${e.message}")
            when (e) {
                is NotLoggedInError -> throw e
            }
        }
        return false
    }

//    override suspend fun refreshAccount(): Boolean {
//        try {
//            withContext(Dispatchers.IO) {
//                if (characterService.update()) {
//                    storageService.updateAll()
//                }
//            }
//            return true
//        } catch (e: Exception) {
//            when (e) {
//                is NotLoggedInError -> throw e
//                else -> {
//                    Timber.d("Failed to update all storages::${e.message}")
//                }
//            }
//        }
//        return false
//    }
}