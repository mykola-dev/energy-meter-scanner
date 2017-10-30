package ds.meterscanner.mvvm.viewmodel

import L
import android.graphics.Bitmap
import android.text.format.DateUtils
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.google.firebase.auth.FirebaseUser
import ds.bindingtools.binding
import ds.meterscanner.R
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.mvvm.BindableViewModel
import ds.meterscanner.mvvm.Command
import ds.meterscanner.mvvm.MainView
import ds.meterscanner.mvvm.invoke
import ds.meterscanner.util.post

class MainViewModel(kodein: Kodein) : BindableViewModel(kodein) {

    var buttonsEnabled: Boolean by binding()
    var lastUpdated: String by binding()
    var apiKey: String by binding()
    var toolbarSubtitle: String by binding()

    val onLoggedInCommand = Command<Unit>()
    val runAlarmsCommand = Command<Unit>()

    private var jobsChecked = false
    var jobId: Int = -1
    var isIntentConsumed = false

    val appVersion: String = instance("version")

    init {
        toggleProgress(true)
    }

    override fun onLoggedIn(user: FirebaseUser) {
        L.v("signed in")
        // workaround
        post { toggleProgress(false) }

        db.keepSynced(true)

        prepareApiKey()

        updateLastSnapshot()

        toolbarSubtitle = user.email ?: ""

        checkTasks()
        onLoggedInCommand()
    }

    private fun prepareApiKey() = async {
        try {
            apiKey = prefs.apiKey()
            L.v("anyline key=$apiKey")
        } catch (e: Exception) {
            showSnackbarCommand(getString(R.string.api_key_not_found))

        }
    }

    private fun updateLastSnapshot() = async {
        val snapshot: Snapshot = db.getLatestSnapshot()
        lastUpdated = "${getString(R.string.latest_shot)} ${DateUtils.getRelativeTimeSpanString(snapshot.timestamp)}"
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
        buttonsEnabled = !enabled
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

    fun onCameraButton(view: MainView) = view.navigateCameraScreen()
    fun onListsButton(view: MainView) = view.navigateListsScreen()
    fun onChartsButton(view: MainView) = view.navigateChartsScreen()
    fun onSettingsButton(view: MainView) = view.navigateSettingsScreen()
}