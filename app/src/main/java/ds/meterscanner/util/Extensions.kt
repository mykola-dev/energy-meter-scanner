package ds.meterscanner.util

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import ds.meterscanner.App

val Context.app: App
    get() = this.applicationContext as App

fun Context.toast(text: CharSequence): Unit = Toast.makeText(this.applicationContext, text, Toast.LENGTH_SHORT).show()

fun Double.abs() = Math.abs(this)
fun Int.abs() = Math.abs(this)
fun Long.abs() = Math.abs(this)
fun Float.abs() = Math.abs(this)

fun Number.toDips(context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)
