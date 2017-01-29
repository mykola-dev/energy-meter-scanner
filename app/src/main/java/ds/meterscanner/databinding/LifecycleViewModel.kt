package ds.meterscanner.databinding

import android.support.annotation.CallSuper
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

abstract class LifeCycleViewModel : ViewModel {

    private val lifecycleSubject = BehaviorSubject.create<ViewModelEvent>()

    @CallSuper
    override fun onCreate() {
        lifecycleSubject.onNext(ViewModelEvent.CREATE)
    }

    @CallSuper
    override fun onAttach() {
        lifecycleSubject.onNext(ViewModelEvent.ATTACH)
    }

    @CallSuper
    override fun onDetach() {
        lifecycleSubject.onNext(ViewModelEvent.DETACH)
    }

    @CallSuper
    override fun onDestroy() {
        lifecycleSubject.onNext(ViewModelEvent.DESTROY)
    }

    fun <T : Any?> bindUntilEvent(event: ViewModelEvent): LifecycleTransformer<T> = RxLifecycle.bindUntilEvent<T, ViewModelEvent>(lifecycleSubject, event)

    fun <T> Flowable<T>.bindTo(event: ViewModelEvent): Flowable<T> = compose(bindUntilEvent(event))
    fun Completable.bindTo(event: ViewModelEvent): Completable = compose(bindUntilEvent<Any>(event))
    fun <T> Single<T>.bindTo(event: ViewModelEvent): Single<T> = compose(bindUntilEvent<T>(event))
    fun <T> Observable<T>.bindTo(event: ViewModelEvent): Observable<T> = compose(bindUntilEvent<T>(event))

    enum class ViewModelEvent {
        CREATE,
        ATTACH,
        DETACH,
        DESTROY
    }
}
