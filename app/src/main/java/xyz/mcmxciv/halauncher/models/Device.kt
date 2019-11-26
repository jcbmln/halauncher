package xyz.mcmxciv.halauncher.models

import java.util.*

data class Device(
    val appId: String,
    val appName: String,
    val appVersion: String,
    val deviceName: String,
    val manufacturer: String,
    val model: String,
    val osName: String,
    val osVersion: String,
    val supportsEncryption: Boolean,
    val appData: Dictionary<String, Objects>?
)