package xyz.mcmxciv.halauncher.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.mcmxciv.halauncher.utils.ResourceProvider

object SettingsRepositorySpec : Spek({
    val sharedPreferences by memoized { mockk<SharedPreferences>(relaxed = true) }
    val resourceProvider by memoized { mockk<ResourceProvider>(relaxed = true) }
    val settingsRepository by memoized { SettingsRepository(sharedPreferences, resourceProvider) }

    describe("instance url") {
        context("set url") {
            val url = "http://home-assistant.halauncher.app"

            beforeEachTest {
                settingsRepository.instanceUrl = url
            }

            it("should save the url to shared preferences") {
                verify {
                    sharedPreferences.edit { putString(SettingsRepository.INSTANCE_URL_KEY, url) }
                }
            }
        }
    }
})
