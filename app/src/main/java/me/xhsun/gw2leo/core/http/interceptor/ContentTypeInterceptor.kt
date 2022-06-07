package me.xhsun.gw2leo.core.http.interceptor

import me.xhsun.gw2leo.core.config.CONTENT_TYPE
import me.xhsun.gw2leo.core.config.CONTENT_VALUE
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ContentTypeInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val request = original.newBuilder()
            .header(CONTENT_TYPE, CONTENT_VALUE)
            .method(original.method, original.body)
            .build()

        return chain.proceed(request)
    }
}