package me.xhsun.gw2leo.http.interceptor

import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.config.AUTH_BODY_FORMAT
import me.xhsun.gw2leo.config.AUTH_HEADER
import me.xhsun.gw2leo.config.NO_AUTH_INJECTION
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(private val accountService: IAccountService) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.pathSegments.lastOrNull() ?: ""
        if (NO_AUTH_INJECTION.contains(path)) {
            return chain.proceed(original)
        }

        val token = accountService.api()
        if (token.isEmpty()) {
            throw NotLoggedInError()
        }

        val request = original.newBuilder()
            .header(AUTH_HEADER, AUTH_BODY_FORMAT.format(token))
            .method(original.method, original.body)
            .build()
        return chain.proceed(request)
    }
}