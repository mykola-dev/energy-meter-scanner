package ds.meterscanner.di

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Provides
    @Singleton
    fun database(ctx: Context): FirebaseDatabase {
        FirebaseApp.initializeApp(ctx)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun auth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun analytics(ctx: Context) = FirebaseAnalytics.getInstance(ctx)

    @Provides
    @Singleton
    fun storage() = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun remoteConfig() = FirebaseRemoteConfig.getInstance()

}