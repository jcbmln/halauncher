package xyz.mcmxciv.halauncher.data.repositories

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.BatteryManager
import timber.log.Timber
import xyz.mcmxciv.halauncher.data.models.Sensor
import javax.inject.Inject

class SensorRepository @Inject constructor(private val context: Context) {
    fun getBatterySensor(): Sensor? {
        return context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

            if (level == -1 || scale == -1) {
                Timber.e("Could not get battery level.")
                return null
            }

            val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL
            val chargerType =
                when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                    BatteryManager.BATTERY_PLUGGED_AC -> "AC"
                    BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
                    else -> "N/A"
                }

            val chargeLevel = (level.toFloat() / scale.toFloat() * 100f).toInt()
            var icon = "mdi:battery"
            if (isCharging) icon += "-charging"
            if (chargerType == "Wireless") icon += "-wireless"

            val batteryStep = chargeLevel / 10
            icon += when (batteryStep) {
                0 -> "-outline"
                10 -> ""
                else -> "-${batteryStep}0"
            }

            return Sensor(
                "battery_level",
                "sensor",
                chargeLevel,
                icon,
                mapOf(
                    "is_charging" to isCharging,
                    "charger_type" to chargerType
                )
            )
        }
    }

    fun getNetworkSensor(): Sensor {
        val wifiManager =
            context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connection = wifiManager.connectionInfo

        val ssid = if (connection.networkId == -1) {
            "<not connected>"
        } else {
            connection.ssid.removePrefix("\"").removeSuffix("\"")
        }

        val lastScanStrength = wifiManager.scanResults.firstOrNull {
            it.BSSID == connection.bssid
        }?.level ?: -1

        var signalStrength = -1
        if (lastScanStrength != -1) {
            signalStrength = WifiManager.calculateSignalLevel(lastScanStrength, 4)
        }

        val icon = "mdi:wifi-strength-" + when (signalStrength) {
            -1 -> "off"
            0 -> "outline"
            else -> signalStrength
        }

        return Sensor(
            "wifi_connection",
            "sensor",
            ssid,
            icon,
            mapOf(
                "bssid" to connection.bssid,
                "ip_address" to formatIPAddress(connection.ipAddress),
                "link_speed" to connection.linkSpeed,
                "is_hidden" to connection.hiddenSSID,
                "frequency" to connection.frequency,
                "signal_level" to lastScanStrength
            )
        )
    }

    private fun formatIPAddress(ip: Int): String =
        "${(ip and 0xff)}.${(ip shr 8 and 0xff)}.${(ip shr 16 and 0xff)}.${(ip shr 24 and 0xff)}"
}