package ds.meterscanner.data

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat

class ResourceProvider(val context:Context) {

    fun getString(@StringRes id: Int): String = context.getString(id)

    fun getColor(@ColorRes id: Int): Int = ContextCompat.getColor(context,id)
}