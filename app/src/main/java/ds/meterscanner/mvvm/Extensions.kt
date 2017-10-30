package ds.meterscanner.mvvm

import L
import android.arch.lifecycle.*
import android.support.annotation.MainThread
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import java.util.concurrent.atomic.AtomicBoolean

inline fun <reified T : ViewModel> FragmentActivity.viewModelOf(factory: ViewModelProvider.Factory? = null): T {
    return if (factory != null)
        ViewModelProviders.of(this, factory)[T::class.java]
    else
        ViewModelProviders.of(this)[T::class.java]
}

inline fun <reified T : ViewModel> FragmentActivity.viewModelOf(crossinline factory: () -> T): T {
    return viewModelOf(object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = factory() as T
    })
}

inline fun <reified T : ViewModel> Fragment.viewModelOf(): T = ViewModelProviders.of(this)[T::class.java]

fun <T> LiveData<T>.observe(owner: LifecycleOwner, block: (T) -> Unit) = observe(owner, Observer { block(it!!) })

operator fun Command<Unit>.invoke() {
    value = Unit
}

/**
 * One-shot command
 */
open class Command<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        if (hasActiveObservers()) {
            L.w("${javaClass.simpleName}: Multiple observers registered but only one will be notified of changes.")
        }

        super.observe(owner, Observer {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    @MainThread
    override fun setValue(t: T) {
        pending.set(true)
        super.setValue(t)
    }

    override fun getValue(): T = super.getValue()!!

    @MainThread
    operator fun invoke(param: T) {
        value = param
    }

}