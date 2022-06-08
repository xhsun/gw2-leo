package me.xhsun.gw2leo.core.refresh.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.xhsun.gw2leo.core.config.MATERIAL_STORAGE_PREFIX
import me.xhsun.gw2leo.core.config.STORAGE_TYPE_KEY
import me.xhsun.gw2leo.core.refresh.service.IStorageRefreshService
import timber.log.Timber

@HiltWorker
class StorageRefreshWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val storageRefreshService: IStorageRefreshService
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val storageType = inputData.getString(STORAGE_TYPE_KEY)
        if (storageType.isNullOrEmpty()) {
            Timber.d("Storage type is required for refresh worker")
            return Result.failure()
        }
        return withContext(Dispatchers.IO) {
            try {
                Timber.d("Start refresh::$storageType")
                if (storageType.contains(MATERIAL_STORAGE_PREFIX)) {
                    storageRefreshService.updateMaterial()
                } else {
                    storageRefreshService.updateStorage(storageType)
                }
                Timber.d("Refresh successful::$storageType")
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Encountered an error while running refresh::$storageType")
            }
            Result.success()
        }

    }
}