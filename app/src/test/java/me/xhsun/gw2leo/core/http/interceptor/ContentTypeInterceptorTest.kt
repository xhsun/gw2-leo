package me.xhsun.gw2leo.core.http.interceptor

import io.github.serpro69.kfaker.Faker
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import me.xhsun.gw2leo.core.config.CONTENT_TYPE
import me.xhsun.gw2leo.core.config.CONTENT_VALUE
import okhttp3.Interceptor
import okhttp3.Request
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ContentTypeInterceptorTest {
    private val faker = Faker()
    private lateinit var target: ContentTypeInterceptor

    @BeforeEach
    fun setUp() {
        target = ContentTypeInterceptor()
    }

    @Test
    fun `intercept() should inject content type`() {
        val input = mockk<Interceptor.Chain>()
        val inputRequest = Request.Builder().url("http://${faker.internet.domain()}").build()
        val actual = slot<Request>()

        every { input.request() } returns inputRequest
        every { input.proceed(capture(actual)) } returns mockk()

        target.intercept(input)
        Assertions.assertThat(actual.captured.header(CONTENT_TYPE)).isEqualTo(
            CONTENT_VALUE
        )
    }
}