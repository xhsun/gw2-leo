package me.xhsun.gw2leo.storage.service.mediator

import androidx.paging.ExperimentalPagingApi
import io.github.serpro69.kfaker.Faker
import io.mockk.mockk
import me.xhsun.gw2leo.core.refresh.service.IStorageRefreshService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StorageRemoteMediatorBuilderTest {
    private val faker = Faker()
    private lateinit var refreshServiceMock: IStorageRefreshService
    private lateinit var target: StorageRemoteMediatorBuilder

    @BeforeEach
    fun setUp() {
        refreshServiceMock = mockk()
        target = StorageRemoteMediatorBuilder(refreshServiceMock)
    }

    @Test
    @OptIn(ExperimentalPagingApi::class)
    fun build() {
        val input = faker.random.randomString()
        val actual = target.build(input)
        assertThat(actual).isInstanceOf(StorageRemoteMediator::class.java)
    }
}