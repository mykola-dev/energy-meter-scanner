/*
 * Copyright (c) 2016. Deviant Studio
 */

package ds.meterscanner.di

import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.Glide
import com.evernote.android.job.JobManager
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.androidActivityScope
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.GsonBuilder
import ds.meterscanner.App
import ds.meterscanner.BuildConfig
import ds.meterscanner.auth.Authenticator
import ds.meterscanner.data.Prefs
import ds.meterscanner.data.ResourceProvider
import ds.meterscanner.db.FirebaseDb
import ds.meterscanner.net.NetLayer
import ds.meterscanner.net.WeatherRestApi
import ds.meterscanner.scheduler.Scheduler
import ds.meterscanner.scheduler.SnapshotJobCreator
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

fun App.mainComponent(app: App) = Kodein {
    registerActivityLifecycleCallbacks(androidActivityScope.lifecycleManager)
    bind<Context>() with singleton { app }
    import(networkModule)
    import(firebaseModule)
    import(authModule)
    import(schedulerModule)
    import(activityModule)
    import(miscModule)

}

val activityModule = Kodein.Module {
    bind<String>("tag") with autoScopedSingleton(androidActivityScope) { it.javaClass.simpleName }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

val networkModule = Kodein.Module {
    bind() from singleton { NetLayer(instance(), instance()) }
    bind() from singleton { GsonBuilder().create() }
    bind("APPID") from instance("3a2aae4aa5a564852f108fa99754e5f1")
    bind("weather") from singleton {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(StethoInterceptor())

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            builder.addInterceptor(loggingInterceptor)
        }

        builder.addInterceptor { chain ->
            var request = chain.request()
            val url = request
                .url()
                .newBuilder()
                .addQueryParameter("APPID", instance("APPID"))
                .build()
            request = request
                .newBuilder()
                .url(url)
                .build()
            return@addInterceptor chain.proceed(request)
        }

        builder.build()
    }

    bind() from singleton {
        Retrofit.Builder()
            .baseUrl(instance<String>("host"))
            .client(instance("weather"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    bind() from singleton { instance<Retrofit>().create(WeatherRestApi::class.java) }
    constant("host") with "http://api.openweathermap.org/data/2.5/"
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

val schedulerModule = Kodein.Module {
    bind() from singleton { JobManager.create(instance()) }
    bind() from singleton { Scheduler(instance(), instance(), provider()) }

    onReady {
        instance<JobManager>().addJobCreator(SnapshotJobCreator())
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@SuppressLint("MissingPermission")
val firebaseModule = Kodein.Module {
    bind() from singleton { FirebaseDatabase.getInstance() }
    bind() from singleton { FirebaseAuth.getInstance() }
    bind() from singleton { FirebaseDb(instance(), instance(), instance(), instance()) }
    bind() from singleton { FirebaseAnalytics.getInstance(instance()) }
    bind() from singleton { FirebaseStorage.getInstance() }
    bind() from singleton { FirebaseRemoteConfig.getInstance() }

    // init Firebase
    onReady {
        FirebaseApp.initializeApp(instance())
        instance<FirebaseDatabase>().setPersistenceEnabled(true)
    }
}

val authModule = Kodein.Module {
    bind() from singleton { Authenticator(instance(), instance()) }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

val miscModule = Kodein.Module {
    bind() from singleton { Glide.with(instance<Context>()) }
    bind() from singleton { Prefs(instance(), instance()) }
    bind() from provider {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal
    }
    bind("version") from singleton {
        val context: Context = instance()
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName
    }

    bind() from singleton { ResourceProvider(instance()) }

    onReady {
        launch(UI) {
            instance<Prefs>().fetchRemote()
        }
    }
}