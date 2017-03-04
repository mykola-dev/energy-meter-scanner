package ds.meterscanner

import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.evernote.android.job.JobManager
import com.facebook.stetho.Stetho
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import ds.meterscanner.data.Prefs
import ds.meterscanner.di.mainComponent
import ds.meterscanner.scheduler.SnapshotJobCreator
import timber.log.Timber

class App : MultiDexApplication(), KodeinAware {

    override val kodein = mainComponent(this)

    val jobManager: JobManager by kodein.lazy.instance()
    val prefs: Prefs by kodein.lazy.instance()

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initStetho()
        initJobManager()

        // enable vector drawables in the resources
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // refresh remote prefs
        prefs.fetchRemote().subscribe()
    }

    private fun initJobManager() {
        jobManager.addJobCreator(SnapshotJobCreator())
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
