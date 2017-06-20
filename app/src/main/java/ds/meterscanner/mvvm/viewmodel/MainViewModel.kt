package ds.meterscanner.mvvm.viewmodel

import L
import android.app.Application
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.text.format.DateUtils
import android.view.Menu
import com.github.salomonbrys.kodein.erased.instance
import com.google.firebase.auth.FirebaseUser
import ds.meterscanner.R
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.mvvm.BaseViewModel3
import ds.meterscanner.mvvm.Command
import ds.meterscanner.mvvm.invoke
import ds.meterscanner.util.post

class MainViewModel(app: Application) : BaseViewModel3(app) {

    var buttonsEnabled = ObservableField<Boolean>()
    var apiKeyReady = ObservableField<Boolean>()
    var lastUpdated = ObservableField("")

    val onLoggedInCommand = Command<Unit>()
    val runAlarmsCommand = Command<Unit>()

    var jobsChecked = false
    var apiKey: String? = null
    var jobId: Int = -1
    var isIntentConsumed = false

    private val appVersion: String = instance("version")

    init {
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

        toolbar.subtitle = user.email

        checkTasks()
        onLoggedInCommand()
    }

    fun prepareApiKey() = async {
        try {
            apiKey = prefs.apiKey()
            apiKeyReady.set(true)
            L.v("anyline key=$apiKey")
        } catch(e: Exception) {
            showSnackbarCommand(getString(R.string.api_key_not_found))

        }
    }

    private fun updateLastSnapshot() = async {
        val snapshot: Snapshot = db.getLatestSnapshot()
        lastUpdated.set("${getString(R.string.latest_shot)} ${DateUtils.getRelativeTimeSpanString(snapshot.timestamp)}")
    }

    private fun checkTasks() {
        if (scheduler.getScheduledJobs().size == 0 && jobId < 0 && !jobsChecked) {
            jobsChecked = true

            showSnackbarCommand(getString(R.string.empty_jobs_message), actionText = R.string.go, actionCallback = {
                runAlarmsCommand()
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

    fun onNewData(value: Double, bitmap: Bitmap, corrected: Boolean) = async {
        val s = Snapshot(value, prefs.currentTemperature.toInt())
        val url = if (prefs.saveImages)
            db.uploadImage(bitmap, s.timestamp.toString())
        else
            ""
        s.image = url
        db.saveSnapshot(s)
        val message = "$value " + if (corrected) "(corrected)" else ""
        showSnackbarCommand(message, actionText = R.string.discard, actionCallback = {
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