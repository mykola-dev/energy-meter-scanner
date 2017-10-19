package ds.meterscanner.mvvm

import L
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.support.annotation.StringRes
import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.erased.instance
import com.google.firebase.auth.FirebaseUser
import ds.meterscanner.R
import ds.meterscanner.auth.Authenticator
import ds.meterscanner.data.Prefs
import ds.meterscanner.data.ResourceProvider
import ds.meterscanner.db.FirebaseDb
import ds.meterscanner.mvvm.viewmodel.ToolbarViewModel
import ds.meterscanner.net.NetLayer
import ds.meterscanner.scheduler.Scheduler
import ds.meterscanner.ui.Progressable
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

@SuppressLint("StaticFieldLeak")
abstract class BaseViewModel : ViewModel(), KodeinGlobalAware, Progressable {

    val restService: NetLayer = instance()
    val prefs: Prefs = instance()
    val authenticator: Authenticator = instance()
    val db: FirebaseDb = instance()
    val scheduler: Scheduler = instance()
    val resources: ResourceProvider = instance()

    val toolbar = ToolbarViewModel()
    val showProgress = ObservableBoolean()

    // commands
    open val runAuthScreenCommand: Command<Unit>? = Command()
    val finishCommand = Command<Unit>()
    val showSnackbarCommand = SnackBarCommand()

    private var progressStopSignal = Job()
    var lifecycleJob = Job() // create a job object to manage lifecycle

    init {
        authenticator.startListen(this, { logged ->
            if (logged)
                onLoggedIn(authenticator.getUser()!!)
            else
                runAuthScreenCommand?.invoke()
        })

    }

    override fun onCleared() {
        super.onCleared()
        authenticator.stopListen(this)
        lifecycleJob.cancel()
        progressStopSignal.cancel()
    }

    protected open fun onLoggedIn(user: FirebaseUser) {}

    /**
     * Delayed progress
     */
    override fun toggleProgress(enabled: Boolean) {
        launch(UI + progressStopSignal) {
            L.i("toggle progress: $enabled")
            if (enabled) {
                delay(200)
            } else {
                progressStopSignal.cancel()
                progressStopSignal = Job()
            }
            showProgress.set(enabled)
        }
    }

    fun async(showErrors: Boolean = true, withProgress: Boolean = true, block: suspend CoroutineScope.() -> Unit) {
        if (withProgress)
            toggleProgress(true)

        launch(UI + lifecycleJob, block = {
            try {
                block()
            } catch (e: Exception) {
                L.w(e)
                if (showErrors) {
                    onErrorSnack(e)
                } else throw e
            } finally {
                if (withProgress)
                    toggleProgress(false)
            }
        })
    }

    protected fun onErrorSnack(t: Throwable) = showSnackbarCommand(t.message ?: getString(R.string.error_unknown))

    protected fun getString(@StringRes id: Int): String = resources.getString(id)

}
