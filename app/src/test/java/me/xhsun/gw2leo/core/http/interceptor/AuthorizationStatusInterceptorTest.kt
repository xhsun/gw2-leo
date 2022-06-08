package me.xhsun.gw2leo.core.http.interceptor

import io.github.serpro69.kfaker.Faker
import io.mockk.every
import io.mockk.mockk
import me.xhsun.gw2leo.account.error.NotLoggedInError
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AuthorizationStatusInterceptorTest {
    private val faker = Faker()
    private lateinit var target: AuthorizationStatusInterceptor

    @BeforeEach
    fun setUp() {
        target = AuthorizationStatusInterceptor()
    }

    @Test
    fun `intercept() should proceed`() {
        val input = mockk<Interceptor.Chain>()
        val inputRequest = Request.Builder().url("http://${faker.internet.domain()}").build()
        val response = mockk<Response>()
        every { input.request() } returns inputRequest
        every { input.proceed(any()) } returns response
        every { response.code } returns 200

        assertDoesNotThrow {
            target.intercept(input)
        }
    }

    @Test
    fun `intercept() should not proceed`() {
        val input = mockk<Interceptor.Chain>()
        val inputRequest = Request.Builder().url("http://${faker.internet.domain()}").build()
        val response = mockk<Response>()
        every { input.request() } returns inputRequest
        every { input.proceed(any()) } returns response
        every { response.code } returns 401

        assertThrows<NotLoggedInError> {
            target.intercept(input)
        }
    }
}