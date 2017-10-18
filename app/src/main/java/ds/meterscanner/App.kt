package ds.meterscanner

import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.evernote.android.job.JobManager
import com.facebook.stetho.Stetho
import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.squareup.leakcanary.LeakCanary
import ds.meterscanner.data.Prefs
import ds.meterscanner.di.setupGlobalKodein
import ds.meterscanner.scheduler.SnapshotJobCreator
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import timber.log.Timber

class App : MultiDexApplication(), KodeinGlobalAware {

    private val jobManager: JobManager by kodein.lazy.instance()
    val prefs: Prefs by kodein.lazy.instance()

    override fun onCreate() {
        super.onCreate()
        //initLeakCanary() || return
        setupGlobalKodein(this)
        initTimber()
        initStetho()
        initJobManager()

        // enable vector drawables in the resources
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // refresh remote prefs
        launch(UI) {
            prefs.fetchRemote()
        }
    }

    private fun initLeakCanary(): Boolean {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return false
        }
        LeakCanary.install(this)
        return true
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
