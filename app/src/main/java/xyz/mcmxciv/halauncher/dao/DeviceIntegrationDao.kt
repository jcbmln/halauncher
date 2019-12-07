package xyz.mcmxciv.halauncher.dao

import androidx.room.*
import xyz.mcmxciv.halauncher.models.DeviceIntegration

@Dao
interface DeviceIntegrationDao {
    @Query("SELECT * FROM device_integrations LIMIT 1")
    suspend fun getDevice(): DeviceIntegration?

    @Insert
    suspend fun insertDevice(device: DeviceIntegration)

    @Update
    suspend fun updateDevice(device: DeviceIntegration)

    @Delete
    suspend fun deleteDevice(device: DeviceIntegration)
}