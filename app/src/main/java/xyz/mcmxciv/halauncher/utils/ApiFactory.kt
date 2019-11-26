package xyz.mcmxciv.halauncher.utils

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiFactory {
//    fun getAuthenticationApi(baseUrl: String): AuthenticationApi {
//        return Retrofit.Builder()
//            .baseUrl(baseUrl)
//            .addConverterFactory(MoshiConverterFactory.create())
//            .build()
//            .create(AuthenticationApi::class.java)
//    }
//
//    fun getIntegrationApi(baseUrl: String): IntegrationApi {
//        return Retrofit.Builder()
//            .baseUrl(baseUrl)
//            .addConverterFactory(MoshiConverterFactory.create())
//            .build()
//            .create(IntegrationApi::class.java)
//    }

    fun <T> createApi(baseUrl: String, api: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(api)
    }
}