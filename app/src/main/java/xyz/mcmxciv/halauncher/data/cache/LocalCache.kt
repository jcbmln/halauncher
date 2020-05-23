package xyz.mcmxciv.halauncher.data.cache

import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import xyz.mcmxciv.halauncher.domain.models.Session
import xyz.mcmxciv.halauncher.domain.models.WebhookInfo
import xyz.mcmxciv.halauncher.ui.HassTheme

interface LocalCache {
    var instanceUrl: String
    var session: Session?
    var deviceInfo: DeviceInfo?
    var webhookInfo: WebhookInfo?
    var deviceName: String
    var sensorIds: Set<String>
    var sensorUpdateInterval: Int
    val hasHomeAssistantInstance: Boolean
    val isAuthenticated: Boolean
    var theme: HassTheme?
}
