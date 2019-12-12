package xyz.mcmxciv.halauncher.di.modules

//@Module
//class DbModule(private val context: Context) {
//    @Provides
//    @Reusable
//    internal fun provideDatabase(): AppDatabase {
//        return Room.databaseBuilder(
//            context,
//            AppDatabase::class.java,
//            "appDb").build()
//    }
//
//    @Provides
//    @Reusable
//    internal fun provideSessionDao(db: AppDatabase) = db.sessionDao()
//}