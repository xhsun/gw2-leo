package me.xhsun.gw2leo.core.http

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import me.xhsun.gw2leo.BuildConfig
import me.xhsun.gw2leo.core.config.BASE_URL
import me.xhsun.gw2leo.core.config.TIMEOUT
import me.xhsun.gw2leo.core.http.interceptor.AuthorizationInterceptor
import me.xhsun.gw2leo.core.http.interceptor.AuthorizationStatusInterceptor
import me.xhsun.gw2leo.core.http.interceptor.ContentTypeInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GW2RepositoryFactory @Inject constructor(
    authInterceptor: AuthorizationInterceptor,
    contentTypeInterceptor: ContentTypeInterceptor,
    authorizationStatusInterceptor: AuthorizationStatusInterceptor
) : IGW2RepositoryFactory {
    private val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    private val retrofit: Retrofit
    private val client: Lazy<IGW2Repository>
    private val logger: HttpLoggingInterceptor
        get() {
            val loggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                loggingInterceptor.apply { level = HttpLoggingInterceptor.Level.BODY }
            }
            return loggingInterceptor
        }

    init {
        okHttpBuilder.addInterceptor(contentTypeInterceptor)
        okHttpBuilder.addInterceptor(authInterceptor)
        okHttpBuilder.addInterceptor(logger)
        okHttpBuilder.addInterceptor(authorizationStatusInterceptor)
        okHttpBuilder.connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpBuilder.readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
        val okHttp = okHttpBuilder.build()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL).client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
            .build()
        client = lazy { retrofit.create(IGW2Repository::class.java) }
    }

    override fun gw2Repository(): IGW2Repository {
        return client.value
    }

    private fun getMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}