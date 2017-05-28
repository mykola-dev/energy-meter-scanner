package ds.meterscanner.mvvm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar

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

operator fun Command<Unit>.invoke() {
    value = Unit
}

class SnackBarCommand : Command<SnackBarCommand.Params>() {

    operator fun invoke(text: String,
                        callback: (() -> Unit)? = null,
                        duration: Int = Snackbar.LENGTH_LONG,
                        @StringRes actionText: Int = 0,
                        actionCallback: (() -> Unit)? = null) {
        value = Params(text, callback, duration, actionText, actionCallback)
    }

    class Params(
        val text: String,
        val callback: (() -> Unit)? = null,
        val duration: Int = Snackbar.LENGTH_LONG,
        @StringRes val actionText: Int = 0,
        val actionCallback: (() -> Unit)? = null
    )
}

class RunCameraScreenCommand : Command<RunCameraScreenCommand.Params>() {
    operator fun invoke(tries: Int, jobId: Int) {
        value = Params(tries, jobId)
    }

    class Params(
        val tries: Int,
        val jobId: Int
    )
}