package me.xhsun.gw2leo.storage.ui.model

import android.os.StrictMode
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.github.serpro69.kfaker.Faker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.core.config.STORAGE_DISPLAY_KEY
import me.xhsun.gw2leo.core.refresh.service.IStorageRefreshService
import me.xhsun.gw2leo.storage.service.IStorageRepository
import me.xhsun.gw2leo.storage.ui.adapter.StorageAdapter
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
internal class StorageViewModelTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()
    private lateinit var storageRepositoryMock: IStorageRepository
    private lateinit var savedStateHandleMock: SavedStateHandle
    private lateinit var stateMock: MutableLiveData<StorageDisplay>
    private lateinit var adapterMock: StorageAdapter
    private lateinit var target: StorageViewModel

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var storageRefreshServiceMock: IStorageRefreshService

    @Before
    fun setUp() {
        StrictMode.enableDefaults()
        hiltRule.inject()

        storageRepositoryMock = mockk()
        savedStateHandleMock = mockk(relaxed = true)
        stateMock = MutableLiveData()
        coEvery {
            savedStateHandleMock.getLiveData<StorageDisplay>(STORAGE_DISPLAY_KEY)
        } returns stateMock
        adapterMock = mockk(relaxed = true)

        val config = Configuration.Builder()
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(
            ApplicationProvider.getApplicationContext(),
            config
        )

        target = StorageViewModel(storageRepositoryMock, savedStateHandleMock)
    }


    @Test
    fun `update() should update on new state`() {
        val input = dataFaker.storageDisplayFaker()
        every { savedStateHandleMock.get<StorageDisplay>(STORAGE_DISPLAY_KEY) } returns dataFaker.storageDisplayFaker()
        target.update(input, adapterMock)
        assertThat(target.storageLoading).isTrue
    }

    @Test
    fun `update() should not update on same state`() {
        val input = dataFaker.storageDisplayFaker()
        every { savedStateHandleMock.get<StorageDisplay>(STORAGE_DISPLAY_KEY) } returns input
        target.update(input, adapterMock)
        assertThat(target.storageLoading).isFalse
    }

    @Test
    fun hardRefresh(): Unit = runBlocking {
        val input = faker.random.randomString()

        coEvery { storageRefreshServiceMock.updateStorage(input) } returns Unit

        target.hardRefresh(ApplicationProvider.getApplicationContext(), input).observeForever {
            assertThat(it).isInstanceOf(ListenableWorker.Result.Success::class.java)
        }
    }

    @Test
    fun `changeState() should reset error and stop loading`() {
        val input = mockk<RecyclerView>(relaxed = true)
        val inputState = mockk<CombinedLoadStates>(relaxed = true)

        every { inputState.refresh } returns mockk<LoadState.NotLoading>(relaxed = true)
        every { inputState.append.endOfPaginationReached } returns true
        every { adapterMock.itemCount } returns faker.random.nextInt(1, 10)

        target.update(dataFaker.storageDisplayFaker(), adapterMock)
        target.storageLoading = true
        target.storageErrMsg = faker.random.randomString()

        target.changeState(input, inputState)
        assertThat(target.storageLoading).isFalse
        assertThat(target.storageErrMsg).isEmpty()
    }

    @Test
    fun `changeState() should show error when not loading and list empty`() {
        val input = mockk<RecyclerView>(relaxed = true)
        val inputState = mockk<CombinedLoadStates>(relaxed = true)

        every { inputState.refresh } returns mockk<LoadState.NotLoading>(relaxed = true)
        every { inputState.prepend.endOfPaginationReached } returns true
        every { adapterMock.itemCount } returns 0

        target.update(dataFaker.storageDisplayFaker(), adapterMock)

        target.changeState(input, inputState)
        assertThat(target.storageLoading).isFalse
        assertThat(target.storageErrMsg).isNotEmpty
    }

    @Test
    fun `changeState() should show error when received loading error`() {
        val input = mockk<RecyclerView>(relaxed = true)
        val inputState = mockk<CombinedLoadStates>(relaxed = true)
        val err = mockk<LoadState.Error>(relaxed = true)

        every { inputState.refresh } returns err
        every { err.error.message } returns faker.random.randomString()

        target.storageLoading = true
        target.storageErrMsg = ""

        target.changeState(input, inputState)
        assertThat(target.storageLoading).isFalse
        assertThat(target.storageErrMsg).isNotEmpty
    }

    @Test
    fun `changeState() should reset error but continue loading`() {
        val input = mockk<RecyclerView>(relaxed = true)
        val inputState = mockk<CombinedLoadStates>(relaxed = true)

        every { inputState.refresh } returns mockk<LoadState.Loading>(relaxed = true)

        target.storageLoading = true
        target.storageErrMsg = faker.random.randomString()

        target.changeState(input, inputState)
        assertThat(target.storageLoading).isTrue
        assertThat(target.storageErrMsg).isEmpty()
    }

    @Test
    fun `checkEmpty() should do nothing`() {
        every { adapterMock.itemCount } returns 0

        target.storageLoading = false
        target.storageErrMsg = ""

        target.checkEmpty()
        assertThat(target.storageLoading).isFalse
        assertThat(target.storageErrMsg).isEmpty()
    }

    @Test
    fun `checkEmpty() should show error`() {
        every { adapterMock.itemCount } returns 0

        target.update(dataFaker.storageDisplayFaker(), adapterMock)
        target.storageLoading = false
        target.storageErrMsg = ""

        target.checkEmpty()
        assertThat(target.storageLoading).isFalse
        assertThat(target.storageErrMsg).isNotEmpty
    }

    @Test
    fun `checkEmpty() should reset`() {
        every { adapterMock.itemCount } returns faker.random.nextInt(1, 10)

        target.update(dataFaker.storageDisplayFaker(), adapterMock)
        target.storageLoading = false
        target.storageErrMsg = faker.random.randomString()

        target.checkEmpty()
        assertThat(target.storageLoading).isFalse
        assertThat(target.storageErrMsg).isEmpty()
    }

    @Test
    fun `checkEmpty() should reset due to still loading`() {
        every { adapterMock.itemCount } returns faker.random.nextInt(1, 10)

        target.update(dataFaker.storageDisplayFaker(), adapterMock)
        target.storageErrMsg = faker.random.randomString()

        target.checkEmpty()
        assertThat(target.storageLoading).isTrue
        assertThat(target.storageErrMsg).isEmpty()
    }

    @Test
    fun onRetry() {
        target.update(dataFaker.storageDisplayFaker(), adapterMock)
        target.onRetry()
        assertThat(target.storageLoading).isTrue
        coVerify(exactly = 1) { adapterMock.refresh() }
    }

    @Test
    fun `onRetry() no adapter provided`() {
        target.onRetry()
        assertThat(target.storageLoading).isFalse
        coVerify(exactly = 0) { adapterMock.refresh() }
    }
}