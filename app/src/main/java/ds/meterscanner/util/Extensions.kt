package ds.meterscanner.util

import android.arch.lifecycle.*
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
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

fun <T> LiveData<T>.observe(owner: LifecycleOwner, block: (T?) -> Unit) = observe(owner, Observer { block(it) })

inline fun <reified T : ViewModel> FragmentActivity.provideViewModel(): T = ViewModelProviders.of(this)[T::class.java]
inline fun <reified T : ViewModel> Fragment.provideViewModel(): T = ViewModelProviders.of(this)[T::class.java]