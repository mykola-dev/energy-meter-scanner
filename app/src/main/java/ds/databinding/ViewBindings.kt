/**
 * Experimental databinding tool © 2017 Deviant Studio
 */
package ds.databinding

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

fun <T : Any?> bindTo(prop: KProperty0<T>, setter: (T) -> Unit, getter: (() -> T)? = null) = bindInternal(prop, setter, getter)

fun <T : Any?> Bindable.to(prop: KProperty0<T>, setter: (T) -> Unit, getter: (() -> T)? = null) = bindInternal(prop, setter, getter)
fun <T : Any?> Bindable.to(prop: KProperty0<T>, mutableProp: KMutableProperty0<T>, getter: (() -> T)? = null) = bindInternal(prop, { mutableProp.set(it) }, getter)

fun <T : Bindable> T.bind(builder: T.() -> Unit) = builder(this)

private fun <T : Any?> bindInternal(prop: KProperty0<T>, setter: (T) -> Unit, getter: (() -> T)? = null) {
    val owner: Bindable = (prop as CallableReference).boundReceiver as Bindable
    println("bind ${prop.name}")
    val binding = getBinding<T>(owner, prop) ?: BindingData()
    binding.setters += setter
    binding.field = prop.name
    if (getter != null)
        if (binding.getter == null)
            binding.getter = getter
        else
            error("Only one getter per property allowed")

    setter(prop.get())  // initialize view
    bindings[owner]!!.put(prop.name, binding)
}

operator fun <T : Bindable, P> T.invoke(block: (T) -> KProperty0<P>): Pair<T, KProperty0<P>> = Pair(this, block(this))

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


private class BindingData<T : Any?, R : Any?> {
    var field: String = ""
    var getter: (() -> R)? = null
    val setters = mutableListOf<(T) -> Unit>()
}

interface Bindable
