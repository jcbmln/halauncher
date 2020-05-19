package xyz.mcmxciv.halauncher.data.authentication

import android.content.SharedPreferences
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import retrofit2.Response
import xyz.mcmxciv.halauncher.data.LocalCache
import xyz.mcmxciv.halauncher.data.models.Token
import xyz.mcmxciv.halauncher.domain.models.Session
import xyz.mcmxciv.halauncher.domain.models.TokenResult
import xyz.mcmxciv.halauncher.utils.ResourceProvider

object AuthenticationRepositorySpec : Spek({
    val authenticationApi by memoized { mockk<AuthenticationApi>() }
    val sharedPreferences by memoized { mockk<SharedPreferences>(relaxed = true) }
    val resourceProvider by memoized { mockk<ResourceProvider>(relaxed = true) }
    val localCache by memoized { LocalCache(resourceProvider, sharedPreferences) }
    val authenticationRepository by memoized {
        AuthenticationRepository(authenticationApi, localCache)
    }

    describe("authentication repository") {
        context("on authentication") {
            describe("get token") {
                lateinit var tokenResult: Any
                beforeEachTest {
                    coEvery {
                        authenticationApi.getToken(
                            AuthenticationRepository.GRANT_TYPE_CODE,
                            "123456",
                            AuthenticationRepository.CLIENT_ID
                        )
                    } returns Response.success(Token(
                        "ABCDEFGH",
                        1800,
                        "IJKLMNOPQRST",
                        "Bearer"
                    ))

                    tokenResult = runBlocking {
                        authenticationRepository.getToken("123456")
                    }
                }

                it("should return a success token result") {
                    assertThat(tokenResult is TokenResult.Success).isTrue()
                }
            }

            describe("create session") {
                val token = Token(
                    "ABCDEFGH",
                    1800,
                    "IJKLMNOPQRST",
                    "Bearer"
                )
                beforeEachTest {
                    runBlocking {
                        authenticationRepository.createSession(token)
                    }
                }

                it("should create and save a session") {
                    verify { localCache.session = Session(token) }
                }
            }
        }
    }
})
