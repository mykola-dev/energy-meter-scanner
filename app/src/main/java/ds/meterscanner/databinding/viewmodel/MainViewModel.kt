package ds.meterscanner.databinding.viewmodel

import L
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.text.format.DateUtils
import android.view.Menu
import com.github.salomonbrys.kodein.erased.instance
import com.google.firebase.auth.FirebaseUser
import ds.meterscanner.R
import ds.meterscanner.databinding.BaseViewModel
import ds.meterscanner.databinding.MainView
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.util.post

class MainViewModel(v: MainView, var jobId: Int) : BaseViewModel<MainView>(v) {

    val buttonsEnabled = ObservableBoolean()
    val apiKeyReady = ObservableBoolean()
    val lastUpdated = ObservableField<String>()

    var jobsChecked = false
    var apiKey: String? = null

    private val appVersion: String = instance("version")

    override fun onCreate() {
        super.onCreate()
        toggleProgress(true)
    }

    override fun onLoggedIn(user: FirebaseUser) {
        L.v("signed in")
        post {
            // workaround
            toggleProgress(false)
        }
        db.keepSynced(true)

        prepareApiKey()

        updateLastSnapshot()

        toolbar.subtitle.set(user.email)

        checkTasks()
        view.onLoggedIn()
    }

    fun prepareApiKey() = async {
        try {
            apiKey = prefs.apiKey()
            apiKeyReady.set(true)
            L.v("anyline key=$apiKey")
        } catch(e: Exception) {
            view.showSnackbar(view.getString(R.string.api_key_not_found))

        }
    }

    private fun updateLastSnapshot() = async {
        val snapshot: Snapshot = db.getLatestSnapshot()
        lastUpdated.set("${view.getString(R.string.latest_shot)} ${DateUtils.getRelativeTimeSpanString(snapshot.timestamp)}")
    }

    private fun checkTasks() {
        if (scheduler.getScheduledJobs().size == 0 && jobId < 0 && !jobsChecked) {
            jobsChecked = true

            view.showSnackbar(view.getString(R.string.empty_jobs_message), actionText = R.string.go, actionCallback = {
                view.runAlarms()
            })
        }
    }

    fun logout() {
        authenticator.signOut()
        toggleProgress(true)
        scheduler.clearAllJobs()
        prefs.clearAll()
    }

    override fun toggleProgress(enabled: Boolean) {
        super.toggleProgress(enabled)
        buttonsEnabled.set(!enabled)
    }

    fun onCameraButton() = view.runCamera(tries = 1, jobId = -1)
    fun onListsButton() = view.runHistory()
    fun onChartsButton() = view.runCharts()
    fun onSettingsButton() = view.runSettings()

    fun takeSnapshot(jobId: Int) {
        view.runCamera(prefs.scanTries, jobId)
    }

    fun onNewData(value: Double, bitmap: Bitmap, corrected: Boolean) = async {
        val s = Snapshot(value, prefs.currentTemperature.toInt())
        val url = if (prefs.saveImages)
            db.uploadImage(bitmap, s.timestamp.toString())
        else
            ""
        s.image = url
        db.saveSnapshot(s)
        val message = "$value " + if (corrected) "(corrected)" else ""
        view.showSnackbar(message, actionText = R.string.discard, actionCallback = {
            db.deleteSnapshots(listOf(s))
        })
        updateLastSnapshot()
    }

    override fun onPrepareMenu(menu: Menu) {
        super.onPrepareMenu(menu)
        menu.findItem(R.id.action_version).title = "v$appVersion"

        menu.findItem(R.id.action_logout).setOnMenuItemClickListener {
            logout()
            true
        }
    }
}