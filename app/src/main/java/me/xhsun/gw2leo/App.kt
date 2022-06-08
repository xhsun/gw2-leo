package me.xhsun.gw2leo

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import me.xhsun.gw2leo.core.config.MIN_REFRESH_RATE_HR
import me.xhsun.gw2leo.core.config.REFRESH_RATE_HR
import me.xhsun.gw2leo.core.config.REFRESH_WORK_TAG
import me.xhsun.gw2leo.core.refresh.work.RefreshWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
open class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return if (BuildConfig.DEBUG) {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .setWorkerFactory(workerFactory)
                .build()
        } else {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.ERROR)
                .setWorkerFactory(workerFactory)
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(true)
            .setRequiresStorageNotLow(true)
            .build()

        val refreshWork =
            PeriodicWorkRequestBuilder<RefreshWorker>(REFRESH_RATE_HR.toLong(), TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInitialDelay(MIN_REFRESH_RATE_HR.toLong(), TimeUnit.HOURS)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            REFRESH_WORK_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            refreshWork
        )

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}