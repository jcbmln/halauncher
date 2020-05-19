package xyz.mcmxciv.halauncher.sensors

import android.content.Context
import android.net.wifi.WifiManager
import xyz.mcmxciv.halauncher.domain.models.Sensor
import xyz.mcmxciv.halauncher.domain.models.SensorInfo

class NetworkSensorManager : SensorManager {
    override fun getSensorInfo(context: Context): List<SensorInfo> {
        return listOf(
            SensorInfo(
                getWifiConnectionSensor(context),
                "Wi-Fi Connection"
            )
        )
    }

    override fun getSensors(context: Context): List<Sensor> {
        return listOf(getWifiConnectionSensor(context))
    }

    private fun getWifiConnectionSensor(context: Context): Sensor {
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
