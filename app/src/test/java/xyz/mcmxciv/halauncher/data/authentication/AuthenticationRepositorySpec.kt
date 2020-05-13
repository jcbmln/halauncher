package xyz.mcmxciv.halauncher.data.authentication

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.lang.Exception

object AuthenticationRepositorySpec : Spek({
    val authenticationApi by memoized { mockk<AuthenticationApi>() }
    val authenticationRepository by memoized { AuthenticationRepository(authenticationApi) }

    describe("#${AuthenticationRepository::getToken.name}") {
        context("error") {
            beforeEachTest {
                coEvery {
                    authenticationApi.getToken(any(), any(), any())
                } throws Exception()
            }

            it("should give exception") {
                val caughtThrowable = catchThrowable {
                    runBlocking {
                        authenticationRepository.getToken("123456")
                    }
                }

                assertThat(caughtThrowable).isNotNull()
            }
        }
    }
})