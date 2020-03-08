package xyz.mcmxciv.halauncher.data.interactors

import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.data.IntegrationException
import xyz.mcmxciv.halauncher.data.repositories.AuthenticationRepository
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import java.lang.Exception
import javax.inject.Inject

class UrlInteractor @Inject constructor(
    private val localStorageRepository: LocalStorageRepository
) {
    var baseUrl: String
        get() = localStorageRepository.baseUrl
        set(value) { localStorageRepository.baseUrl = value }

    val externalAuthUrl: String
        get() = localStorageRepository.baseUrl.toHttpUrl()
            .newBuilder()
            .addEncodedQueryParameter("external_auth", "1")
            .build()
            .toString()

    val authenticationUrl: String
        get() = localStorageRepository.baseUrl.toHttpUrl()
            .newBuilder()
            .addPathSegments("auth/authorize")
            .addEncodedQueryParameter("response_type",
                AuthenticationRepository.RESPONSE_TYPE
            )
            .addEncodedQueryParameter("client_id",
                AuthenticationRepository.CLIENT_ID
            )
            .addEncodedQueryParameter("redirect_uri",
                AuthenticationRepository.REDIRECT_URI
            )
            .build()
            .toString()
}