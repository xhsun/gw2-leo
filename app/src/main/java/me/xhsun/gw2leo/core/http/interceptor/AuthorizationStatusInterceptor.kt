package me.xhsun.gw2leo.core.http.interceptor

import me.xhsun.gw2leo.account.error.NotLoggedInError
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthorizationStatusInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val response = chain.proceed(chain.request())
        if (response.code == 401) {
            throw NotLoggedInError()
        }
        return response
    }
}