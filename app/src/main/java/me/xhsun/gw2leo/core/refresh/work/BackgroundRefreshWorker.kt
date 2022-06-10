package me.xhsun.gw2leo.core.refresh.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.core.refresh.service.IAccountRefreshService
import me.xhsun.gw2leo.core.refresh.service.IStorageRefreshService
import me.xhsun.gw2leo.registry.IoDispatcher
import retrofit2.HttpException
import timber.log.Timber

@HiltWorker
class BackgroundRefreshWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val accountRefreshService: IAccountRefreshService,
    private val storageRefreshService: IStorageRefreshService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            try {
                accountRefreshService.update()
                storageRefreshService.updateAll()
                Timber.d("Background refresh successful")
                Result.success()
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Encountered an error while running background refresh")
                when (throwable) {
                    is HttpException -> Result.retry()
                    is NotLoggedInError -> Result.success()
                    else -> {
                        if (throwable.cause != null && throwable.cause is NotLoggedInError) {
                            Result.success()
                        } else {
                            Result.failure()
                        }
                    }
                }
            }
        }
    }
}