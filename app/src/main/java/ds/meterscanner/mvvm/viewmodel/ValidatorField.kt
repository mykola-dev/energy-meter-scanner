package ds.meterscanner.mvvm.viewmodel

import android.databinding.ObservableField

class ValidatorField(
    private val field: ObservableField<String>,
    private val validator: (String) -> String
) : ObservableField<String>() {

    private fun validate(text: String): Boolean {
        val message = validator(text)
        set(message)
        return message.isEmpty()
    }

    fun validate(): Boolean = validate(field.get())
}