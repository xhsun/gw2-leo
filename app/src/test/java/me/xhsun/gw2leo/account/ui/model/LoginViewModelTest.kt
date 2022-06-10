package me.xhsun.gw2leo.account.ui.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.g00fy2.quickie.QRResult
import io.github.serpro69.kfaker.Faker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import me.xhsun.gw2leo.InstantExecutorExtension
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.core.refresh.service.IAccountRefreshService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
internal class LoginViewModelTest {
    private val faker = Faker()
    private lateinit var refreshServiceMock: IAccountRefreshService
    private lateinit var target: LoginViewModel

    @Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @BeforeEach
    fun setUp() {
        refreshServiceMock = mockk()
        target = LoginViewModel(refreshServiceMock, Dispatchers.IO)
    }

    @Test
    fun `onTextChange() should clear error message`() {
        target.errMsg = faker.random.randomString()
        target.onTextChange()
        assertThat(target.errMsg).isEmpty()
    }

    @Test
    fun `onEnter() should update when value exist`(): Unit = runBlocking {
        val expected = UIState(shouldTransfer = true)
        val input = faker.random.randomString()
        target.api.set(input)

        coEvery { refreshServiceMock.initialize(input) } returns Unit

        target.states.observeForever {
            assertThat(it).isEqualTo(expected)
        }
        target.onEnter()
    }

    @Test
    fun `onEnter() should not update when value empty`(): Unit = runBlocking {
        target.api.set("")

        target.onEnter()
        assertThat(target.errMsg).isNotEmpty()
    }

    @Test
    fun `onEnter() should not update when value null`(): Unit = runBlocking {
        target.api.set(null)

        target.onEnter()
        assertThat(target.errMsg).isNotEmpty()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onEnter() should not transfer when failed initialize`(): Unit = runTest {
        val input = faker.random.randomString()
        target.api.set(input)

        coEvery { refreshServiceMock.initialize(input) } throws NotLoggedInError()

        target.onEnter()
        delay(100)
        assertThat(target.errMsg).isNotEmpty()
    }

    @Test
    fun `handleQR() should update when QR read success`() {
        val expected = faker.random.randomString()
        val input = mockk<QRResult.QRSuccess>()
        every { input.content.rawValue } returns expected
        target.handleQR(input)
        assertThat(target.errMsg).isEmpty()
        assertThat(target.api.get()).isEqualTo(expected)
    }

    @Test
    fun `handleQR() should not update when QR read failed`() {
        val input = mockk<QRResult.QRUserCanceled>()

        target.states.observeForever {
            assertThat(it.snackbarText).isNotEmpty()
        }

        target.handleQR(input)
    }
}