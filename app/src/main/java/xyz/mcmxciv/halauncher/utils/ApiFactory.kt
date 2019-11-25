package xyz.mcmxciv.halauncher.utils

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import xyz.mcmxciv.halauncher.AuthenticationApi

object ApiFactory {
    fun getAuthenticationApi(baseUrl: String): AuthenticationApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AuthenticationApi::class.java)
    }
}