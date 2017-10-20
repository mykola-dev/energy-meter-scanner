/**
 * Experimental databinding tool © 2017 Deviant Studio
 */
@file:Suppress("FINAL_UPPER_BOUND")

package ds.databinding

import android.widget.CompoundButton
import android.widget.TextView
import java.util.*
import kotlin.jvm.internal.CallableReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

private val bindings = WeakHashMap<Bindable, MutableMap<String, BindingData<*, *>>>()

inline fun <reified T : Any> binding(initialValue: T? = null): ReadWriteProperty<Bindable, T> = BindingProperty(initialValue, T::class)

class BindingProperty<T : Any>(private var value: T?, private val type: KClass<T>) : ReadWriteProperty<Bindable, T> {

    override fun getValue(thisRef: Bindable, property: KProperty<*>): T {
        val b = getBinding<T>(thisRef, property)?.getter
        return b?.invoke() ?: value ?: default(type)
    }

    override fun setValue(thisRef: Bindable, property: KProperty<*>, value: T) {
        val oldValue = this.value
        if (oldValue != value) {
            this.value = value
            getBinding<T>(thisRef, property)?.setters?.forEach { it(value) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun default(cls: KClass<T>): T = when (cls) {
        String::class -> "" as T
        CharSequence::class -> "" as T
        java.lang.Integer::class -> 0 as T
        java.lang.Boolean::class -> false as T
        java.lang.Float::class -> 0f as T
        java.lang.Double::class -> 0.0 as T
        else -> cls.java.newInstance()
    }
}

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
private inline fun <T> getBinding(vm: Bindable, prop: KProperty<*>): BindingData<T, T>? =
    bindings.getOrPut(vm, { mutableMapOf() })[prop.name] as BindingData<T, T>?

fun <T : Any?> Bindable.bind(prop: KProperty0<T>, mutableProp: KMutableProperty0<T>, getter: (() -> T)? = null) =
    bind(prop, { mutableProp.set(it) }, getter)

/**
 * Binds [TextView] to the  [CharSequence] field
 */
inline fun <reified T : CharSequence> Bindable.bindCharSequence(prop: KProperty0<T>, view: TextView) =
    bind(prop, view::setText, view::getText)

/**
 * Binds [TextView] to the  [String] field
 */
inline fun <reified T : String> Bindable.bind(prop: KProperty0<T>, view: TextView) =
    bind(prop, view::setText, { view.text.toString() as T })

/**
 * Binds [CompoundButton] to the  [Boolean] field
 */
inline fun <reified T : Boolean> Bindable.bind(prop: KProperty0<T>, view: CompoundButton) =
    bind(prop, view::setChecked, view::isChecked)

fun <T : Any?> Bindable.bind(prop: KProperty0<T>, setter: (T) -> Unit, getter: (() -> T)? = null) {
    val owner: Bindable = prop.parent as Bindable
    println("bind ${prop.name}")
    val binding = getBinding<T>(owner, prop) ?: BindingData(prop.name)
    binding.setters += setter
    if (getter != null)
        if (binding.getter == null)
            binding.getter = getter
        else
            error("Only one getter per property allowed")

    setter(prop.get())  // initialize view
    bindings[owner]!!.put(prop.name, binding)
}


fun <T : Any?> Bindable.unbind(prop: KProperty<T>) {
    bindings[this]?.remove(prop.name)
}

fun Bindable.unbindAll() {
    bindings.remove(this)
}

fun Bindable.debugBindings() {
    bindings[this]?.forEach { e ->
        println("for ${e.value.field}: id=${e.key} getter=${e.value.getter} setters=${e.value.setters.size}")
    }
}

private val <T> KProperty0<T>.parent get() = (this as CallableReference).boundReceiver

private class BindingData<T : Any?, R : Any?>(val field: String) {
    var getter: (() -> R)? = null
    val setters = mutableListOf<(T) -> Unit>()
}

interface Bindable
