package ds.bindingtools

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import kotlin.reflect.KProperty

fun bundle(f: BundleBuilder.() -> Unit): Bundle {
    val builder = BundleBuilder()
    f(builder)
    return builder.build()
}

@Suppress("MemberVisibilityCanPrivate", "UNCHECKED_CAST")
class BundleBuilder {

    private val bundle = Bundle()

    fun build() = bundle

    internal infix fun String.to(arg: Any?) {
        when (arg) {
            is String? -> bundle.putString(this, arg)
            is Int -> bundle.putInt(this, arg)
            is IntArray? -> bundle.putIntArray(this, arg)
            is Long -> bundle.putLong(this, arg)
            is LongArray? -> bundle.putLongArray(this, arg)
            is Boolean -> bundle.putBoolean(this, arg)
            is BooleanArray? -> bundle.putBooleanArray(this, arg)
            is CharSequence? -> bundle.putCharSequence(this, arg)
            is Float -> bundle.putFloat(this, arg)
            is FloatArray? -> bundle.putFloatArray(this, arg)
            is Double -> bundle.putDouble(this, arg)
            is DoubleArray? -> bundle.putDoubleArray(this, arg)
            is Byte -> bundle.putByte(this, arg)
            is ByteArray? -> bundle.putByteArray(this, arg)
            is Parcelable? -> bundle.putParcelable(this, arg)
            is Serializable? -> bundle.putSerializable(this, arg)
            is Array<*>? -> bundle.putStringArray(this, arg as Array<out String>)
            is ArrayList<*>? -> bundle.putStringArrayList(this, arg as ArrayList<String>)
            else -> error("Failed to bind the arg")
        }
    }

    internal infix fun KProperty<*>.to(arg: Any?) {
        name to arg
    }

}