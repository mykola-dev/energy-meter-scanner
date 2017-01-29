package ds.meterscanner.databinding.viewmodel

import L
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.text.format.DateUtils
import android.view.Menu
import com.github.salomonbrys.kodein.instance
import com.google.firebase.auth.FirebaseUser
import ds.meterscanner.R
import ds.meterscanner.databinding.BaseViewModel
import ds.meterscanner.databinding.MainView
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.util.post
import io.reactivex.Single

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

        prefs.apiKey().subscribe({
            apiKeyReady.set(true)
            apiKey = it
            L.v("anyline key=$it")
        }, {
            view.showSnackbar(view.getString(R.string.api_key_not_found))
        })

        updateLastSnapshot()

        toolbar.subtitle.set(user.email)

        checkTasks()
        view.onLoggedIn()
    }

    private fun updateLastSnapshot() {
        db.getLatestSnapshot().subscribe({
            lastUpdated.set("${view.getString(R.string.latest_shot)} ${DateUtils.getRelativeTimeSpanString(it.timestamp)}")
        })
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

    fun onNewData(value: Double, bitmap: Bitmap, corrected: Boolean) {
        val s = Snapshot(value, prefs.currentTemperature.toInt())
        (if (prefs.saveImages) db.uploadImageRx(bitmap, s.timestamp.toString()) else Single.just(""))
            .onErrorReturn { "" }
            .bindTo(ViewModelEvent.DESTROY)
            .subscribe({
                s.image = it
                db.saveSnapshot(s)
                val message = "$value " + if (corrected) "(corrected)" else ""
                view.showSnackbar(message, actionText = R.string.discard, actionCallback = {
                    db.deleteSnapshots(listOf(s))
                })
                updateLastSnapshot()
            }, Throwable::printStackTrace
            )
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