package xyz.mcmxciv.halauncher.models

import com.squareup.moshi.Moshi

abstract class Model {
    companion object {
        inline fun <reified T : Model> fromJson(json: String): T? {
            val adapter = Moshi.Builder().build().adapter(T::class.java)
            return adapter.fromJson(json)
        }
    }
}

inline fun <reified T : Model> Model.toJson(): String {
    val adapter = Moshi.Builder().build().adapter(T::class.java)
    return adapter.toJson(this as T)
}