package me.xhsun.gw2leo.core.http.interceptor

import io.github.serpro69.kfaker.Faker
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.core.config.AUTH_HEADER
import me.xhsun.gw2leo.core.config.NO_AUTH_INJECTION
import okhttp3.Interceptor
import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AuthorizationInterceptorTest {
    private val faker = Faker()
    private lateinit var accountServiceMock: IAccountService
    private lateinit var target: AuthorizationInterceptor

    @BeforeEach
    fun setUp() {
        accountServiceMock = mockk()
        target = AuthorizationInterceptor(accountServiceMock)
    }

    @Test
    fun `intercept() should inject auth token`() {
        val input = mockk<Interceptor.Chain>()
        val inputRequest = Request.Builder().url("http://${faker.internet.domain()}").build()
        val expected = faker.random.randomString()
        val actual = slot<Request>()

        every { input.request() } returns inputRequest
        every { accountServiceMock.api() } returns expected
        every { input.proceed(capture(actual)) } returns mockk()

        target.intercept(input)
        assertThat(actual.captured.header(AUTH_HEADER)).containsIgnoringCase(expected)
    }

    @Test
    fun `intercept() should not inject due to path ignored`() {
        val input = mockk<Interceptor.Chain>()
        val inputRequest =
            Request.Builder().url("http://${faker.internet.domain()}/${NO_AUTH_INJECTION[0]}")
                .build()
        val actual = slot<Request>()

        every { input.request() } returns inputRequest
        every { input.proceed(capture(actual)) } returns mockk()

        target.intercept(input)
        assertThat(actual.captured.header(AUTH_HEADER)).isNullOrEmpty()
    }

    @Test
    fun `intercept() should not inject due to not logged in`() {
        val input = mockk<Interceptor.Chain>()
        val inputRequest = Request.Builder().url("http://${faker.internet.domain()}").build()

        every { input.request() } returns inputRequest
        every { accountServiceMock.api() } returns ""

        assertThrows<NotLoggedInError> {
            target.intercept(input)
        }
    }
}