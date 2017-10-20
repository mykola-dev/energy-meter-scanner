package ds.meterscanner.mvvm.viewmodel

import ds.databinding.Bindable
import ds.databinding.binding
import kotlin.reflect.KProperty0

class Validator(
    private val prop: KProperty0<String>,
    private val validator: (String) -> String
) : Bindable {
    var error: String by binding("")

    fun validate() = validate(prop.get())

    private fun validate(text: String): Boolean {
        error = validator(text)
        return error.isEmpty()
    }
}