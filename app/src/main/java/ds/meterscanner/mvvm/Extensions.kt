package ds.meterscanner.mvvm

import android.arch.lifecycle.*
import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

inline fun <reified T : ViewModel> FragmentActivity.viewModelOf(): T = ViewModelProviders.of(this)[T::class.java]
inline fun <reified T : ViewModel> Fragment.viewModelOf(): T = ViewModelProviders.of(this)[T::class.java]

fun <T> LiveData<T>.observe(owner: LifecycleOwner, block: (T?) -> Unit) = observe(owner, Observer { block(it) })

operator fun Command<Unit>.invoke() {
    value = Unit
}

/**
 * One-shot command
 */
open class Command<T> : LiveData<T>() {
    operator fun invoke(param: T) {
        value = param
    }

    override public fun setValue(value: T?) {
        super.setValue(value)
        if (this.value != null)
            this.value = null   // immediatelly reset value to not repeat it on screen rotate
    }

    fun observe(owner: LifecycleOwner, block: (T) -> Unit) =
        super.observe(owner, android.arch.lifecycle.Observer { if (it != null) block(it) })

    override fun observe(owner: LifecycleOwner?, observer: Observer<T>?) {
        error("Unsupported Operation")
    }

}