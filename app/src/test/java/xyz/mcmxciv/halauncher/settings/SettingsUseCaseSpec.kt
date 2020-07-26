package xyz.mcmxciv.halauncher.settings

import android.content.SharedPreferences
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.mcmxciv.halauncher.utils.ResourceProvider

object SettingsUseCaseSpec : Spek({
    val sharedPreferences by memoized { mockk<SharedPreferences>(relaxed = true) }
    val resourceProvider by memoized { mockk<ResourceProvider>(relaxed = true) }
    val settingsRepository by memoized { SettingsRepository(sharedPreferences, resourceProvider) }
    val settingsUseCase by memoized { SettingsUseCase(settingsRepository) }

    describe("save instance url") {
        context("given valid url") {
            val url = "http://home-assistant.halauncher.app"

            beforeEachTest {
                settingsUseCase.saveInstanceUrl(url)
            }

            it("should store url") {
                verify {
                    settingsRepository.connectionUrl = url
                }
            }
        }

        context("given invalid url") {
            val url = "asdf"
            lateinit var throwable: Throwable

            beforeEachTest {
                throwable = catchThrowable {
                    settingsUseCase.saveInstanceUrl(url)
                }
            }

            it("should throw IllegalArgumentException") {
                assertThat(throwable is IllegalArgumentException).isTrue()
            }
        }
    }
})
