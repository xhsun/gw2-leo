package me.xhsun.gw2leo.core.refresh.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.serpro69.kfaker.Faker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.xhsun.gw2leo.core.config.MATERIAL_STORAGE_PREFIX
import me.xhsun.gw2leo.core.config.STORAGE_TYPE_KEY
import me.xhsun.gw2leo.core.refresh.service.IStorageRefreshService
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class StorageRefreshWorkerTest {
    private val faker = Faker()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var storageRefreshServiceServiceMock: IStorageRefreshService
    private lateinit var targetBuilder: TestListenableWorkerBuilder<StorageRefreshWorker>


    @Before
    fun setUp() {
        storageRefreshServiceServiceMock = mockk()
        storageRefreshServiceServiceMock = mockk()
        targetBuilder = TestListenableWorkerBuilder<StorageRefreshWorker>(
            context = ApplicationProvider.getApplicationContext()
        )
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker {
                    return StorageRefreshWorker(
                        appContext,
                        workerParameters,
                        storageRefreshServiceServiceMock
                    )
                }
            })
    }

    @Test
    fun doWorkUpdateMaterial(): Unit = runBlocking {
        val input = Data.Builder()
        input.putString(STORAGE_TYPE_KEY, MATERIAL_STORAGE_PREFIX)

        coEvery { storageRefreshServiceServiceMock.updateMaterial() } returns Unit

        val target = targetBuilder.setInputData(input.build()).build()
        val actual = target.doWork()

        Assertions.assertThat(actual).isInstanceOf(ListenableWorker.Result.Success::class.java)
        coVerify(exactly = 1) { storageRefreshServiceServiceMock.updateMaterial() }
    }

    @Test
    fun doWorkUpdateStorage(): Unit = runBlocking {
        val inputType = faker.random.randomString()
        val input = Data.Builder()
        input.putString(STORAGE_TYPE_KEY, inputType)

        coEvery { storageRefreshServiceServiceMock.updateStorage(inputType) } returns Unit

        val target = targetBuilder.setInputData(input.build()).build()
        val actual = target.doWork()

        Assertions.assertThat(actual).isInstanceOf(ListenableWorker.Result.Success::class.java)
        coVerify(exactly = 1) { storageRefreshServiceServiceMock.updateStorage(inputType) }
    }

    @Test
    fun doWorkFailedUpdate(): Unit = runBlocking {
        val inputType = faker.random.randomString()
        val input = Data.Builder()
        input.putString(STORAGE_TYPE_KEY, inputType)

        coEvery { storageRefreshServiceServiceMock.updateStorage(inputType) } throws Exception()

        val target = targetBuilder.setInputData(input.build()).build()
        val actual = target.doWork()

        Assertions.assertThat(actual).isInstanceOf(ListenableWorker.Result.Success::class.java)
    }
}