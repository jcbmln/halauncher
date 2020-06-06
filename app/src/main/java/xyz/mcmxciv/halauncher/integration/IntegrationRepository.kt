package xyz.mcmxciv.halauncher.integration

import android.content.SharedPreferences
import androidx.core.content.edit
import xyz.mcmxciv.halauncher.integration.models.DeviceInfo
import xyz.mcmxciv.halauncher.integration.models.WebhookInfo
import xyz.mcmxciv.halauncher.utils.Serializer
import xyz.mcmxciv.halauncher.utils.deserialize
import xyz.mcmxciv.halauncher.utils.serialize
import javax.inject.Inject

class IntegrationRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val secureIntegrationApi: SecureIntegrationApi
) {
    val webhookInfo: WebhookInfo?
        get() {
            val pref = sharedPreferences.getString(WEBHOOK_INFO_KEY, null)
            return pref?.let { Serializer.deserialize(it) }
        }

    suspend fun registerDevice(deviceInfo: DeviceInfo) {
        val webhookInfo = secureIntegrationApi.registerDevice(deviceInfo)
        sharedPreferences.edit {
            putString(WEBHOOK_INFO_KEY, Serializer.serialize(webhookInfo))
        }
    }

    companion object {
        const val WEBHOOK_INFO_KEY = "webhook_info"
    }
}
