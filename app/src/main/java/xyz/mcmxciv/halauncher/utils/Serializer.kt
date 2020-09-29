package xyz.mcmxciv.halauncher.utils

import com.squareup.moshi.Moshi

object Serializer

inline fun <reified T> Serializer.serialize(obj: T): String {
    val adapter = Moshi.Builder().build().adapter(T::class.java)
    return adapter.toJson(obj)
}

inline fun <reified T> Serializer.deserialize(value: String): T? {
    val adapter = Moshi.Builder().build().adapter(T::class.java)
    return adapter.fromJson(value)
}
