package me.xhsun.gw2leo.core.refresh.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.core.refresh.service.IAccountRefreshService
import me.xhsun.gw2leo.core.refresh.service.IStorageRefreshService
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.HttpException
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
internal class BackgroundRefreshWorkerTest {
    private lateinit var accountRefreshServiceMock: IAccountRefreshService
    private lateinit var storageRefreshServiceServiceMock: IStorageRefreshService

    private lateinit var target: BackgroundRefreshWorker

    @Before
    fun setUp() {
        accountRefreshServiceMock = mockk()
        storageRefreshServiceServiceMock = mockk()
        target = TestListenableWorkerBuilder<BackgroundRefreshWorker>(
            context = ApplicationProvider.getApplicationContext()
        )
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker {
                    return BackgroundRefreshWorker(
                        appContext,
                        workerParameters,
                        accountRefreshServiceMock,
                        storageRefreshServiceServiceMock
                    )
                }
            })
            .build()
    }

    @Test
    fun doWorkSuccess(): Unit = runBlocking {
        coEvery { accountRefreshServiceMock.update() } returns Unit
        coEvery { storageRefreshServiceServiceMock.updateAll() } returns Unit

        val actual = target.doWork()

        Assertions.assertThat(actual).isInstanceOf(ListenableWorker.Result.Success::class.java)
        coVerify(exactly = 1) { accountRefreshServiceMock.update() }
        coVerify(exactly = 1) { storageRefreshServiceServiceMock.updateAll() }
    }

    @Test
    fun doWorkNotLoggedIn(): Unit = runBlocking {
        coEvery { accountRefreshServiceMock.update() } throws NotLoggedInError()

        val actual = target.doWork()

        Assertions.assertThat(actual).isInstanceOf(ListenableWorker.Result.Success::class.java)
    }

    @Test
    fun doWorkNetworkIssue(): Unit = runBlocking {
        coEvery { accountRefreshServiceMock.update() } throws HttpException(
            Response.success(
                200,
                ""
            )
        )

        val actual = target.doWork()

        Assertions.assertThat(actual).isInstanceOf(ListenableWorker.Result.Retry::class.java)
    }

    @Test
    fun doWorkUnknownError(): Unit = runBlocking {
        coEvery { accountRefreshServiceMock.update() } returns Unit
        coEvery { storageRefreshServiceServiceMock.updateAll() } throws Exception()

        val actual = target.doWork()

        Assertions.assertThat(actual).isInstanceOf(ListenableWorker.Result.Failure::class.java)
    }
}