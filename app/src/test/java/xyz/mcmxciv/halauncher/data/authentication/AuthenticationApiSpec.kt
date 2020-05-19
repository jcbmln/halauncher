package xyz.mcmxciv.halauncher.data.authentication

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.mcmxciv.halauncher.data.HomeAssistantMockWebServer
import xyz.mcmxciv.halauncher.data.models.Token

object AuthenticationApiSpec : Spek({
    val mockWebServer by memoized { HomeAssistantMockWebServer(AuthenticationApi::class.java) }

    describe("#${AuthenticationApi::getToken.name}") {
        context("success") {
            lateinit var request: RecordedRequest
            var token: Any? = null

            beforeEachTest {
                mockWebServer.enqueue(
                    200,
                    """{
                      "access_token": "ABCDEFGH",
                      "expires_in": 1800,
                      "refresh_token": "IJKLMNOPQRST",
                      "token_type": "Bearer"
                    }"""
                )
                token = runBlocking {
                    mockWebServer.api.getToken(
                        AuthenticationRepository.GRANT_TYPE_CODE,
                        "12345",
                        AuthenticationRepository.CLIENT_ID
                    ).body()!!
                }
                request = mockWebServer.takeRequest()
            }

            it("should create a POST request") {
                assertThat(request.method).isEqualTo("POST")
                assertThat(request.path).isEqualTo("/auth/token")
                assertThat(request.body.readUtf8())
                    .contains("grant_type=authorization_code")
                    .contains("code=12345")
                    .contains("client_id=https%3A%2F%2Fhalauncher.app")
            }

            it("should return a token") {
                assertThat(token).isNotNull
                assertThat(token).isInstanceOf(Token::class.java)
            }
        }
    }

    describe("#${AuthenticationApi::refreshToken.name}") {
        context("success") {
            lateinit var request: RecordedRequest
            var token: Any? = null

            beforeEachTest {
                mockWebServer.enqueue(
                    200,
                    """{
                      "access_token": "ABCDEFGH",
                      "expires_in": 1800,
                      "token_type": "Bearer"
                    }"""
                )
                token = runBlocking {
                    mockWebServer.api.refreshToken(
                        AuthenticationRepository.GRANT_TYPE_REFRESH,
                        "IJKLMNOPQRST",
                        AuthenticationRepository.CLIENT_ID
                    ).body()!!
                }
                request = mockWebServer.takeRequest()
            }

            it("should create a POST request") {
                assertThat(request.method).isEqualTo("POST")
                assertThat(request.path).isEqualTo("/auth/token")
                assertThat(request.body.readUtf8())
                    .contains("grant_type=refresh_token")
                    .contains("refresh_token=IJKLMNOPQRST")
                    .contains("client_id=https%3A%2F%2Fhalauncher.app")
            }

            it("should return a token") {
                assertThat(token).isNotNull
                assertThat(token).isInstanceOf(Token::class.java)
            }
        }

        context("error") {
            lateinit var request: RecordedRequest
            var errorBody: String? = null

            beforeEachTest {
                mockWebServer.enqueue(
                    400,
                    """{
                      "error": "invalid_grant"
                    }"""
                )
                errorBody = runBlocking {
                    mockWebServer.api.refreshToken(
                        AuthenticationRepository.GRANT_TYPE_REFRESH,
                        "IJKLMNOPQRST",
                        AuthenticationRepository.CLIENT_ID
                    ).errorBody()?.charStream()?.readText()
                }
                request = mockWebServer.takeRequest()
            }

            it("should create a POST request") {
                assertThat(request.method).isEqualTo("POST")
                assertThat(request.path).isEqualTo("/auth/token")
                assertThat(request.body.readUtf8())
                    .contains("grant_type=refresh_token")
                    .contains("refresh_token=IJKLMNOPQRST")
                    .contains("client_id=https%3A%2F%2Fhalauncher.app")
            }

            it("should return a token") {
                assertThat(errorBody).isNotNull()
                assertThat(errorBody).contains(
                    """{
                      "error": "invalid_grant"
                    }"""
                )
            }
        }
    }

    describe("#${AuthenticationApi::revokeToken.name}") {
        context("success") {
            lateinit var request: RecordedRequest

            beforeEachTest {
                mockWebServer.enqueue(200)
                runBlocking {
                    mockWebServer.api.revokeToken(
                        "IJKLMNOPQRST",
                        AuthenticationRepository.REVOKE_ACTION
                    )
                }
                request = mockWebServer.takeRequest()
            }

            it("should create a POST request") {
                assertThat(request.method).isEqualTo("POST")
                assertThat(request.path).isEqualTo("/auth/token")
                assertThat(request.body.readUtf8())
                    .contains("token=IJKLMNOPQRST")
                    .contains("action=revoke")
            }
        }
    }
})
