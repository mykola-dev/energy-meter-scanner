package ds.bindingtools

import android.view.View
import ds.meterscanner.databinding.ViewModel
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

private val bindings = WeakHashMap<ViewModel, MutableMap<String, BindingData<*, *>>>()

fun <T : Any?> binding(initialValue: T): ReadWriteProperty<ViewModel, T> = BindingProperty(initialValue)

private class BindingProperty<T : Any?>(initialValue: T) : ReadWriteProperty<ViewModel, T> {
    private var value = initialValue

    override fun getValue(thisRef: ViewModel, property: KProperty<*>): T {
        val b = getBinding<T>(thisRef, property)?.getter
        return b?.invoke() ?: value
    }

    override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T) {
        val oldValue = this.value
        if (oldValue != value) {
            this.value = value
            getBinding<T>(thisRef, property)?.setters?.forEach { it(value) }
        }
    }

}

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
private inline fun <T> getBinding(vm: ViewModel, prop: KProperty<*>): BindingData<T, T>? =
    bindings.getOrPut(vm, { mutableMapOf<String, BindingData<*, *>>() })[prop.name] as BindingData<T, T>?

fun <T : Any?> ViewModel.bind(prop: KProperty0<T>, setter: (T) -> Unit, getter: (() -> T)? = null) {
    println("bind ${prop.name}")
    val binding = getBinding<T>(this, prop) ?: BindingData()
    binding.setters += setter
    binding.field = prop.name
    if (getter != null)
        if (binding.getter == null)
            binding.getter = getter
        else
            error("Only one getter per property allowed")

    setter(prop.get())  // initialize view
    bindings[this]!!.put(prop.name, binding)
}

// alternative bind
@Suppress("unused")
fun <T : Any?> View.bind(vmData: Pair<ViewModel, KProperty0<T>>, setter: (T) -> Unit, getter: (() -> T)? = null) {
    vmData.first.bind(vmData.second, setter, getter)
}

operator fun <T : ViewModel, P> T.invoke(block: (T) -> KProperty0<P>): Pair<T, KProperty0<P>> = Pair(this, block(this))

fun <T : Any?> ViewModel.unbind(prop: KProperty<T>) {
    bindings[this]?.remove(prop.name)
}

fun ViewModel.unbindAll() {
    bindings.remove(this)
}

fun ViewModel.debug() {
    bindings[this]?.forEach { k, v ->
        println("for ${v.field}: id=$k getter=${v.getter} setters=${v.setters.size}")
    }
}


private class BindingData<T : Any?, R : Any?> {
    var field: String = ""
    var getter: (() -> R)? = null
    val setters = mutableListOf<(T) -> Unit>()
}

interface ViewModel
