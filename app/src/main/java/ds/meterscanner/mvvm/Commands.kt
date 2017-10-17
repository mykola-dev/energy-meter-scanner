package ds.meterscanner.mvvm

import android.graphics.Bitmap
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar

class SnackBarCommand : Command<SnackBarCommand.Params>() {

    operator fun invoke(
        text: String,
        callback: (() -> Unit)? = null,
        duration: Int = Snackbar.LENGTH_LONG,
        @StringRes actionText: Int = 0,
        actionCallback: (() -> Unit)? = null
    ) {
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

class FinishWithResultCommand : Command<FinishWithResultCommand.Params>() {
    operator fun invoke(value: Double, bitmap: Bitmap? = null, corrected: Boolean = false) {
        this.value = Params(value, bitmap, corrected)
    }

    class Params(
        val value: Double,
        val bitmap: Bitmap?,
        val corrected: Boolean
    )
}
