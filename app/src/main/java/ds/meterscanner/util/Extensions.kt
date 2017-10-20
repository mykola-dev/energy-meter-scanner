package ds.meterscanner.util

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import ds.meterscanner.App

val Context.app: App
    get() = this.applicationContext as App

fun Context.toast(text: CharSequence): Unit = Toast.makeText(this.applicationContext, text, Toast.LENGTH_SHORT).show()

fun Number.toDips(context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)

var View.visible: Boolean
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else GONE
    }