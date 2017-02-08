package ds.meterscanner.databinding

import L
import android.databinding.ObservableBoolean
import android.view.Menu
import com.google.firebase.auth.FirebaseUser
import ds.meterscanner.auth.Authenticator
import ds.meterscanner.data.Prefs
import ds.meterscanner.data.RefreshEvent
import ds.meterscanner.databinding.viewmodel.ToolbarViewModel
import ds.meterscanner.db.FirebaseDb
import ds.meterscanner.net.NetLayer
import ds.meterscanner.scheduler.Scheduler
import ds.meterscanner.ui.Progressable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class BaseViewModel<out V : BaseView>(final override val view: V) : LifeCycleViewModel(), Progressable {

    @Inject lateinit var  restService: NetLayer
    @Inject lateinit var  prefs: Prefs
    @Inject lateinit var  authenticator: Authenticator
    @Inject lateinit var  db: FirebaseDb
    @Inject lateinit var  scheduler: Scheduler

    val toolbar = ToolbarViewModel()
    val showProgress = ObservableBoolean()

    private val progressStopSignal: PublishSubject<Boolean> = PublishSubject.create()

    override fun onAttach() {
        super.onAttach()

        authenticator.startListen(this, { logged ->
            if (logged)
                onLoggedIn(authenticator.getUser()!!)
            else
                view.runAuthScreen()
        })
    }

    override fun onDetach() {
        super.onDetach()
        authenticator.stopListen(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    protected open fun onLoggedIn(user: FirebaseUser) {}

    /**
     * Delayed progress
     */
    override fun toggleProgress(enabled: Boolean) {
        L.i("toggle progress: $enabled")
        if (enabled) {
            Observable.just(enabled)
                .delay(200, TimeUnit.MILLISECONDS)
                .takeUntil<Boolean>(progressStopSignal)
                .bindTo(ViewModelEvent.DETACH)
                .subscribe {
                    showProgress.set(it)
                }
        }else {
            progressStopSignal.onNext(true)
            showProgress.set(false)
        }
    }

    @Subscribe
    fun onRefresh(e: RefreshEvent) {
    }

    open fun onPrepareMenu(menu: Menu) {}

}

