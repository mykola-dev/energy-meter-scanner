package ds.meterscanner.mvvm

import L
import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import android.support.annotation.StringRes
import android.view.Menu
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.erased.instance
import com.google.firebase.auth.FirebaseUser
import ds.meterscanner.App
import ds.meterscanner.auth.Authenticator
import ds.meterscanner.data.Prefs
import ds.meterscanner.data.RefreshEvent
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
import org.greenrobot.eventbus.Subscribe

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
@SuppressLint("StaticFieldLeak")
abstract class BaseViewModel2(app: Application) : AndroidViewModel(app), KodeinAware, Progressable {

    override val kodein: Kodein = app.appKodein()
    protected val app: App get() = getApplication()

    val restService: NetLayer = instance()
    val prefs: Prefs = instance()
    val authenticator: Authenticator = instance()
    val db: FirebaseDb = instance()
    val scheduler: Scheduler = instance()

    val toolbar = ToolbarViewModel()
    val showProgress = ObservableBoolean()

    // commands
    val runAuthScreenCommand = Command<Unit>()
    val finishCommand = Command<Unit>()
    val showSnackbarCommand = SnackBarCommand()

    //private val progressStopSignal: PublishSubject<Boolean> = PublishSubject.create()
    private var progressStopSignal = Job()
    var job = Job() // create a job object to manage lifecycle

    override fun onCleared() {
        super.onCleared()
        authenticator.stopListen(this)
        job.cancel()
        progressStopSignal.cancel()
    }

    init {
        authenticator.startListen(this, { logged ->
            if (logged)
                onLoggedIn(authenticator.getUser()!!)
            else
                runAuthScreenCommand()
        })

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

    @Subscribe
    fun onRefresh(e: RefreshEvent) {
    }

    open fun onPrepareMenu(menu: Menu) {}

    fun async(block: suspend CoroutineScope.() -> Unit) {
        if (job.isCompleted)
            job = Job()
        launch(UI + job, block = block)
    }

    fun onErrorSnack(t: Throwable) {
        showSnackbarCommand(t.message ?: "Unknown Error")
    }

    protected fun getString(@StringRes id: Int): String = app.getString(id)

}

