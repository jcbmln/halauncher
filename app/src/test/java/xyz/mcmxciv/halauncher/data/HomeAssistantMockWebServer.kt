package xyz.mcmxciv.halauncher.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Url

class HomeAssistantMockWebServer<T>(private val c: Class<T>) {
    private val server = MockWebServer()
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val client = OkHttpClient.Builder()
        .build()
    private val secureClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ABCD")
                .build()
            chain.proceed(newRequest)
        }.build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()
    private val secureRetrofit = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(secureClient)
        .build()
    val api: T
        get() = retrofit.create(c)
    val secureApi: T
        get() = secureRetrofit.create(c)

    fun url(path: String): String =
        server.url(path).toString()

    fun enqueue(code: Int, body: String? = null) {
        val response = MockResponse()
        if (body != null) response.setBody(body)
        server.enqueue(response.setResponseCode(code))
    }

    fun takeRequest() = server.takeRequest()
}