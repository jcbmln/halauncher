package xyz.mcmxciv.halauncher.di

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import xyz.mcmxciv.halauncher.http.SessionInterceptor
import xyz.mcmxciv.halauncher.http.UrlInterceptor
import xyz.mcmxciv.halauncher.settings.SettingsRepository
import xyz.mcmxciv.halauncher.utils.HalauncherResourceProvider
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule(private val context: Context) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    fun appContext(): Context = context

    @Singleton
    @Provides
    fun sharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    fun packageManager(context: Context): PackageManager = context.packageManager

    @Provides
    fun launcherApps(context: Context): LauncherApps =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    @Provides
    fun resourceProvider(context: Context): ResourceProvider =
        HalauncherResourceProvider(context)

    @Singleton
    @SecureApi
    @Provides
    fun secureRetrofit(sharedPreferences: SharedPreferences): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(UrlInterceptor(sharedPreferences))
            .addInterceptor(SessionInterceptor(sharedPreferences))
            .build()

        return Retrofit.Builder()
            .baseUrl(SettingsRepository.PLACEHOLDER_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @Singleton
    @Api
    @Provides
    fun retrofit(sharedPreferences: SharedPreferences): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(UrlInterceptor(sharedPreferences))
            .build()

        return Retrofit.Builder()
            .baseUrl(SettingsRepository.PLACEHOLDER_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }
}
