package xyz.mcmxciv.halauncher.utils

import androidx.room.TypeConverter
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String): Instant {
        return Instant.parse(value)
    }

    @TypeConverter
    fun toTimestamp(value: Instant): String {
        return value.toString()
    }
}
