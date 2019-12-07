package xyz.mcmxciv.halauncher.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_integrations")
data class DeviceIntegration(
    @PrimaryKey @ColumnInfo(name = "webhook_id") val webhookId: String,
    @ColumnInfo(name = "cloudhook_url") val cloudhookUrl: String?,
    @ColumnInfo(name = "remote_ui_url") val remoteUiUrl: String?,
    @ColumnInfo(name = "secret") val secret: String?
)