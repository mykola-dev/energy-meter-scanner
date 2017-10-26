package ds.meterscanner.util

import L
import android.os.Looper

object ThreadTools {

    val isUiThread: Boolean
        get() = Thread.currentThread() === Looper.getMainLooper().thread

    fun logIsUi() {
        L.i("UI thread? %s", isUiThread)
    }
}