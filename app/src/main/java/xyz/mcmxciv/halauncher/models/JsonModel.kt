package xyz.mcmxciv.halauncher.models

import com.squareup.moshi.Moshi

open class JsonModel<T : Model> {
    inline fun <reified M : T> fromJson(json: String): M? {
        val adapter = Moshi.Builder().build().adapter(M::class.java)
        return adapter.fromJson(json)
    }
}

inline fun <reified T : Model> T.toJson(): String {
    val adapter = Moshi.Builder().build().adapter(T::class.java)
    return adapter.toJson(this)
}