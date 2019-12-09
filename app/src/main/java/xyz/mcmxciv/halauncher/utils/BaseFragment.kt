package xyz.mcmxciv.halauncher.utils

import androidx.fragment.app.Fragment
import xyz.mcmxciv.halauncher.di.ApiModule
import xyz.mcmxciv.halauncher.di.AppComponent
import xyz.mcmxciv.halauncher.di.DaggerAppComponent
import xyz.mcmxciv.halauncher.di.DbModule

abstract class BaseFragment : Fragment() {
    protected val component: AppComponent = DaggerAppComponent
        .builder()
        .apiModule(ApiModule())
        .dbModule(DbModule())
        .build()
}