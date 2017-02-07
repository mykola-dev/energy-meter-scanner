package ds.meterscanner.util

import L
import android.os.Handler
import android.os.Looper
import android.support.annotation.ColorRes
import android.text.format.DateFormat
import ds.meterscanner.R

val timeDateFormat = "HH:mm dd.MM.yyyy"
val timeFormat = "HH:mm"

fun formatTimeDate(millis: Long): String = DateFormat.format(timeDateFormat, millis).toString()

fun formatTime(millis: Long): String = DateFormat.format(timeFormat, millis).toString()

// for debug only
fun Long.formatMillis() = DateFormat.format("HH:mm:ss dd.MM.yy", this).toString()

fun post(runnable: () -> Unit) {
    val h = Handler(Looper.getMainLooper())
    h.post(runnable)
}

fun postDelayed(delay: Long, runnable: () -> Unit) {
    val h = Handler(Looper.getMainLooper())
    h.postDelayed(runnable, delay)
}

fun <T> profile(f: () -> T, name: String = ""): T {
    val start = System.currentTimeMillis()
    return try {
        f()
    } finally {
        L.i("profile method [$name] ===> ${System.currentTimeMillis() - start}ms")
    }
}

@ColorRes
fun getColorTemp(temp: Int): Int {
    return if (temp > 10) R.color.temperature_warm
    else if (temp > 0) R.color.temperature_norm
    else if (temp > -10) R.color.temperature_cold
    else if (temp > -20) R.color.temperature_frost
    else R.color.temperature_extreme
}
