package xyz.mcmxciv.halauncher.settings

import android.content.SharedPreferences
import io.mockk.mockk
import org.spekframework.spek2.Spek

object SettingsRepositorySpec : Spek({
    val sharedPreferences by memoized { mockk<SharedPreferences>(relaxed = true) }


})
