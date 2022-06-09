package me.xhsun.gw2leo.storage.service.mediator

import androidx.paging.*
import io.github.serpro69.kfaker.Faker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.core.config.MATERIAL_STORAGE_KEY_FORMAT
import me.xhsun.gw2leo.core.refresh.service.IStorageRefreshService
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MaterialStorageRemoteMediatorTest {
    private val faker = Faker()
    private lateinit var accountServiceMock: IAccountService
    private lateinit var refreshServiceMock: IStorageRefreshService
    private lateinit var target: MaterialStorageRemoteMediator
    private val pagingState =
        PagingState<Int, MaterialStorage>(emptyList(), null, PagingConfig(1), 2)

    @BeforeEach
    fun setUp() {
        accountServiceMock = mockk()
        refreshServiceMock = mockk()
        target = MaterialStorageRemoteMediator(accountServiceMock, refreshServiceMock)
    }

    @Test
    @OptIn(ExperimentalPagingApi::class)
    fun `load() should load new data`(): Unit = runBlocking {
        val input = faker.random.randomString()
        val inputLoadType = LoadType.REFRESH
        val target = MaterialStorageRemoteMediator(accountServiceMock, refreshServiceMock)

        every { accountServiceMock.accountID() } returns input
        coEvery { refreshServiceMock.shouldUpdate(MATERIAL_STORAGE_KEY_FORMAT.format(input)) } returns true
        coEvery { refreshServiceMock.updateMaterial() } returns Unit

        val actual = target.load(inputLoadType, pagingState)
        assertThat(actual)
            .isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
    }

    @Test
    @OptIn(ExperimentalPagingApi::class)
    fun `load() should not load new data due to not refresh`(): Unit = runBlocking {
        val inputLoadType = LoadType.APPEND
        val target = MaterialStorageRemoteMediator(accountServiceMock, refreshServiceMock)

        val actual = target.load(inputLoadType, pagingState)
        assertThat(actual)
            .isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
    }

    @Test
    @OptIn(ExperimentalPagingApi::class)
    fun `load() should not load new data due to shouldn't update`(): Unit = runBlocking {
        val input = faker.random.randomString()
        val inputLoadType = LoadType.REFRESH
        val target = MaterialStorageRemoteMediator(accountServiceMock, refreshServiceMock)

        every { accountServiceMock.accountID() } returns input
        coEvery { refreshServiceMock.shouldUpdate(MATERIAL_STORAGE_KEY_FORMAT.format(input)) } returns false

        val actual = target.load(inputLoadType, pagingState)
        assertThat(actual)
            .isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
    }

    @Test
    @OptIn(ExperimentalPagingApi::class)
    fun `load() should return error`(): Unit = runBlocking {
        val input = faker.random.randomString()
        val inputLoadType = LoadType.REFRESH
        val target = MaterialStorageRemoteMediator(accountServiceMock, refreshServiceMock)

        every { accountServiceMock.accountID() } returns input
        coEvery { refreshServiceMock.shouldUpdate(MATERIAL_STORAGE_KEY_FORMAT.format(input)) } returns true
        coEvery { refreshServiceMock.updateMaterial() } throws Exception()

        val actual = target.load(inputLoadType, pagingState)
        assertThat(actual)
            .isInstanceOf(RemoteMediator.MediatorResult.Error::class.java)
    }
}