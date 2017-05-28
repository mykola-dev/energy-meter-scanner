package ds.meterscanner.mvvm

import L
import android.databinding.BaseObservable
import android.databinding.ObservableBoolean
import android.view.Menu
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.erased.instance
import com.google.firebase.auth.FirebaseUser
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
abstract class BaseViewModel<out V : BaseView>(final override val view: V) : BaseObservable(), ViewModel, KodeinAware, Progressable {

    override val kodein: Kodein = view.kodein

    val restService: NetLayer = instance()
    val prefs: Prefs = instance()
    val authenticator: Authenticator = instance()
    val db: FirebaseDb = instance()
    val scheduler: Scheduler = instance()

    val toolbar = ToolbarViewModel()
    val showProgress = ObservableBoolean()

    //private val progressStopSignal: PublishSubject<Boolean> = PublishSubject.create()
    private var progressStopSignal = Job()
    var job = Job() // create a job object to manage lifecycle

    override fun onCreate() {
    }

    override fun onAttach() {
        authenticator.startListen(this, { logged ->
            if (logged)
                onLoggedIn(authenticator.getUser()!!)
            else
                view.runAuthScreen()
        })
    }

    override fun onDetach() {
        authenticator.stopListen(this)
        job.cancel()
        progressStopSignal.cancel()
    }

    override fun onDestroy() {
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
        launch(UI + job, true, block)
    }

    fun onErrorSnack(t: Throwable) {
        view.showSnackbar(t.message ?: "Unknown Error")
    }

}

