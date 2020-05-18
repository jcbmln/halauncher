package xyz.mcmxciv.halauncher.sensors

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import timber.log.Timber
import xyz.mcmxciv.halauncher.domain.models.Sensor
import xyz.mcmxciv.halauncher.domain.models.SensorInfo

class BatterySensorManager : SensorManager {
    override fun getSensorInfo(context: Context): List<SensorInfo> {
        val sensorInfo = mutableListOf<SensorInfo>()

        context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )?.also {  intent ->
            getBatteryLevelSensor(intent)?.also { sensor ->
                sensorInfo.add(
                    SensorInfo(
                        sensor,
                        "Battery Level",
                        "battery",
                        "%"
                    )
                )
            }

            getBatteryStateSensor(intent)?.also { sensor ->
                sensorInfo.add(
                    SensorInfo(
                        sensor,
                        "Battery State",
                        "battery"
                    )
                )
            }
        }

        return sensorInfo
    }

    override fun getSensors(context: Context): List<Sensor> {
        val sensors = mutableListOf<Sensor>()

        context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )?.also { intent ->
            getBatteryLevelSensor(intent)?.also { sensor ->
                sensors.add(sensor)
            }

            getBatteryStateSensor(intent)?.also { sensor ->
                sensors.add(sensor)
            }
        }

        return sensors
    }

    private fun getBatteryLevelSensor(intent: Intent): Sensor? {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        if (level == -1 || scale == -1) {
            Timber.e("Could not get battery level.")
            return null
        }

        val chargeLevel = getChargeLevel(level, scale)
        val icon = getBatteryIcon(chargeLevel)

        return Sensor(
            "battery_level",
            "sensor",
            chargeLevel,
            icon,
            mapOf()
        )
    }

    private fun getBatteryStateSensor(intent: Intent): Sensor? {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

        if (level == -1 || scale == -1 || status == -1) {
            Timber.e("Could not get battery state.")
            return null
        }

        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL
        val chargerType =
            when (plugged) {
                BatteryManager.BATTERY_PLUGGED_AC -> "ac"
                BatteryManager.BATTERY_PLUGGED_USB -> "usb"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "wireless"
                else -> "unknown"
            }
        val chargingStatus: String = when (status) {
            BatteryManager.BATTERY_STATUS_FULL -> "full"
            BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not_charging"
            else -> "unknown"
        }
        val chargeLevel = getChargeLevel(level, scale)
        val icon = getBatteryIcon(chargeLevel, isCharging, chargerType, chargingStatus)

        return Sensor(
            "battery_state",
            "sensor",
            chargingStatus,
            icon,
            mapOf(
                "is_charging" to isCharging,
                "charger_type" to chargerType
            )
        )
    }

    private fun getChargeLevel(level: Int, scale: Int): Int =
        (level.toFloat() / scale.toFloat() * 100.0f).toInt()

    private fun getBatteryIcon(
        chargeLevel: Int,
        isCharging: Boolean = false,
        chargerType: String? = null,
        chargingStatus: String? = null
    ) : String {
        var icon = "mdi:battery"

        if (chargingStatus == "unknown") {
            icon += "-unknown"
            return icon
        }

        if (isCharging) icon += "-charging"
        if (chargerType == "wireless") icon += "-wireless"

        val batteryStep = chargeLevel / 10
        icon += when (batteryStep) {
            0 -> "-outline"
            10 -> ""
            else -> "-${batteryStep}0"
        }

        return icon
    }
}