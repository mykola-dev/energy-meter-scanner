package ds.meterscanner.databinding

import L
import android.databinding.ObservableBoolean
import android.view.Menu
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.erased.instance
import com.google.firebase.auth.FirebaseUser
import ds.bindingtools.binding
import ds.meterscanner.auth.Authenticator
import ds.meterscanner.data.Prefs
import ds.meterscanner.data.RefreshEvent
import ds.meterscanner.databinding.viewmodel.ToolbarViewModel
import ds.meterscanner.db.FirebaseDb
import ds.meterscanner.net.NetLayer
import ds.meterscanner.scheduler.Scheduler
import ds.meterscanner.ui.Progressable
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.Subscribe

abstract class BaseViewModel<out V : BaseView>(final override val view: V)
    : ViewModel, ds.bindingtools.ViewModel, KodeinAware, Progressable {

    override val kodein: Kodein = view.kodein.value

    val restService: NetLayer = instance()
    val prefs: Prefs = instance()
    val authenticator: Authenticator = instance()
    val db: FirebaseDb = instance()
    val scheduler: Scheduler = instance()

    val toolbar = ToolbarViewModel()
    @Deprecated("use isRefreshing")
    val showProgress = ObservableBoolean()
    var isRefreshing: Boolean by binding(false)

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
        L.i("${javaClass.simpleName} onDetach")
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
        L.i("${javaClass.simpleName} | toggle progress: $enabled")

        launch(UI + progressStopSignal) {
            if (enabled) {
                delay(200)
            } else {
                progressStopSignal.cancel()
                progressStopSignal = Job()
            }
            showProgress.set(enabled)
            isRefreshing = enabled
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
        view.showSnackbar(t.message ?: "Unknown Error")
    }

}

