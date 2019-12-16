package xyz.mcmxciv.halauncher.models

import com.squareup.moshi.Moshi

abstract class BaseModel {
    companion object {
        inline fun <reified T : BaseModel> fromJson(json: String): T? {
            val adapter = Moshi.Builder().build().adapter(T::class.java)
            return adapter.fromJson(json)
        }

        inline fun <reified T : BaseModel> toJson(obj: T): String {
            val adapter = Moshi.Builder().build().adapter(T::class.java)
            return adapter.toJson(obj)
        }
    }
}