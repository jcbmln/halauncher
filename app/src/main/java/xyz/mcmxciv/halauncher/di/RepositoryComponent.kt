package xyz.mcmxciv.halauncher.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, DbModule::class])
interface RepositoryComponent {


    @Component.Builder
    interface Builder {
        fun build(): RepositoryComponent
        fun apiModule(apiModule: ApiModule): Builder
        fun dbModule(dbModule: DbModule): Builder
    }
}