package me.xhsun.gw2leo.storage.service.mediator

import androidx.paging.*
import io.github.serpro69.kfaker.Faker
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.xhsun.gw2leo.core.refresh.service.IStorageRefreshService
import me.xhsun.gw2leo.storage.datastore.entity.Storage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StorageRemoteMediatorTest {
    private val faker = Faker()
    private lateinit var refreshServiceMock: IStorageRefreshService
    private val pagingState = PagingState<Int, Storage>(emptyList(), null, PagingConfig(1), 2)

    @BeforeEach
    fun setUp() {
        refreshServiceMock = mockk()
    }

    @Test
    @OptIn(ExperimentalPagingApi::class)
    fun `load() should load new data`(): Unit = runBlocking {
        val input = faker.random.randomString()
        val inputLoadType = LoadType.REFRESH
        val target = StorageRemoteMediator(input, refreshServiceMock)

        coEvery { refreshServiceMock.shouldUpdate(input) } returns true
        coEvery { refreshServiceMock.updateStorage(input) } returns Unit

        val actual = target.load(inputLoadType, pagingState)
        assertThat(actual).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
    }

    @Test
    @OptIn(ExperimentalPagingApi::class)
    fun `load() should not load due to not refresh`(): Unit = runBlocking {
        val input = faker.random.randomString()
        val inputLoadType = LoadType.APPEND
        val target = StorageRemoteMediator(input, refreshServiceMock)

        val actual = target.load(inputLoadType, pagingState)
        assertThat(actual).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
    }

    @Test
    @OptIn(ExperimentalPagingApi::class)
    fun `load() should not load new data due to shouldn't update`(): Unit = runBlocking {
        val input = faker.random.randomString()
        val inputLoadType = LoadType.REFRESH
        val target = StorageRemoteMediator(input, refreshServiceMock)

        coEvery { refreshServiceMock.shouldUpdate(input) } returns false

        val actual = target.load(inputLoadType, pagingState)
        assertThat(actual).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
    }

    @Test
    @OptIn(ExperimentalPagingApi::class)
    fun `load() should return error`(): Unit = runBlocking {
        val input = faker.random.randomString()
        val inputLoadType = LoadType.REFRESH
        val target = StorageRemoteMediator(input, refreshServiceMock)

        coEvery { refreshServiceMock.shouldUpdate(input) } returns true
        coEvery { refreshServiceMock.updateStorage(input) } throws Exception()

        val actual = target.load(inputLoadType, pagingState)
        assertThat(actual).isInstanceOf(RemoteMediator.MediatorResult.Error::class.java)
    }
}