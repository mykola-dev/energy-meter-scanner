package ds.meterscanner.util

import L
import android.os.Looper
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object ThreadTools {

    private val countDowns = mutableMapOf<Int, CountDownLatch>()

    val isUiThread: Boolean
        get() = Thread.currentThread() === Looper.getMainLooper().thread

    fun lock(id: Int, millis: Long): Boolean {
        val countdown = countDowns.getOrPut(id, { CountDownLatch(1) })
        L.v("threads: lock the $id job thread")
        return countdown.await(millis, TimeUnit.MILLISECONDS)
    }

    fun release(id: Int) {
        L.v("threads: release the $id job thread")
        countDowns[id]?.countDown()
    }

    fun logIsUi() {
        L.i("UI thread? %s", isUiThread)
    }
}