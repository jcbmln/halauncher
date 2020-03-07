package xyz.mcmxciv.halauncher.data.models

import com.squareup.moshi.Moshi

open class SerializerObject<T : SerializableModel> {
    inline fun <reified M : T> fromJson(json: String): M? {
        val adapter = Moshi.Builder().build().adapter(M::class.java)
        return adapter.fromJson(json)
    }
}

inline fun <reified T : SerializableModel> T.toJson(): String {
    val adapter = Moshi.Builder().build().adapter(T::class.java)
    return adapter.toJson(this)
}