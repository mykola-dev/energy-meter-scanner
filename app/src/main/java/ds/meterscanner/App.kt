package ds.meterscanner

import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.facebook.stetho.Stetho
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.squareup.leakcanary.LeakCanary
import ds.meterscanner.di.mainComponent
import timber.log.Timber

class App : MultiDexApplication(), KodeinAware, KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()
    override val kodein: Kodein by injector.kodein()

    override fun onCreate() {
        super.onCreate()
        inject(mainComponent(this))
        //initLeakCanary() || return
        initTimber()
        initStetho()

        // enable vector drawables in the resources
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    private fun initLeakCanary(): Boolean {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated bind LeakCanary for heap analysis.
            // You should not init your app in this process.
            return false
        }
        LeakCanary.install(this)
        return true
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

}
