package ds.meterscanner

import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.evernote.android.job.JobManager
import com.facebook.stetho.Stetho
import ds.meterscanner.data.Prefs
import ds.meterscanner.di.initDagger
import ds.meterscanner.di.mainComponent
import ds.meterscanner.scheduler.SnapshotJobCreator
import timber.log.Timber
import javax.inject.Inject

class App : MultiDexApplication() {

    @Inject lateinit var jobManager: JobManager
    @Inject lateinit var prefs: Prefs

    override fun onCreate() {
        super.onCreate()
        initDagger(this)
        mainComponent.inject(this)
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
