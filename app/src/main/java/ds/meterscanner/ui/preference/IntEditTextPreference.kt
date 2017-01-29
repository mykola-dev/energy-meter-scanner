package ds.meterscanner.ui.preference

import android.content.Context
import android.util.AttributeSet

import com.takisoft.fix.support.v7.preference.EditTextPreference

class IntEditTextPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : EditTextPreference(context, attrs) {

    override fun getPersistedString(defaultReturnValue: String?): String {
        return getPersistedInt(-1).toString()
    }

    override fun persistString(value: String?): Boolean {
        val v = try {
            Integer.valueOf(value)
        } catch (e: Exception) {
            0
        }
        return persistInt(v)
    }
}