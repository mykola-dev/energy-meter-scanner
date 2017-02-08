package ds.meterscanner.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ds.meterscanner.App
import javax.inject.Singleton

@Module
class AppModule(val app: App) {

    @Provides @Singleton fun app() = app
    @Provides @Singleton fun context(): Context = app.applicationContext
}