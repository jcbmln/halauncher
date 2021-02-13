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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
@InstallIn(SingletonComponent::class)
object AppModule {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun sharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    fun packageManager(@ApplicationContext context: Context): PackageManager =
        context.packageManager

    @Provides
    fun launcherApps(@ApplicationContext context: Context): LauncherApps =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    @Provides
    fun resourceProvider(@ApplicationContext context: Context): ResourceProvider =
        HalauncherResourceProvider(context)

    @Singleton
    @SecureApi
    @Provides
    fun secureRetrofit(
        urlInterceptor: UrlInterceptor,
        sessionInterceptor: SessionInterceptor
    ): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(urlInterceptor)
            .addInterceptor(sessionInterceptor)
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
    fun retrofit(urlInterceptor: UrlInterceptor): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(urlInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(SettingsRepository.PLACEHOLDER_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }
}
