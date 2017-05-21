package ds.bindingtools

import android.databinding.BaseObservable
import android.databinding.Observable
import ds.meterscanner.BR
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun <T> BaseObservable.observableField(default: T? = null) = ObservableFieldProperty(this, default)

/*private*/ class ObservableFieldProperty<T>(private val parent: BaseObservable, var current: T?) : ReadWriteProperty<Observable, T?> {

    private var fieldId: Int = -1

    operator fun provideDelegate(thisRef: Observable, property: KProperty<*>): ReadWriteProperty<Observable, T?> {
        fieldId = bindableResourceIdForKProperty(property)
        return this
    }

    override fun getValue(thisRef: Observable, property: KProperty<*>): T? {
        return current
    }

    override fun setValue(thisRef: Observable, property: KProperty<*>, value: T?) {
        if (current != value) {
            current = value
            if (fieldId > 0)
                parent.notifyPropertyChanged(fieldId)
            else
                error("field id is null")

        }
    }
}

private val bindableResourceClass: Class<*> = BR::class.java
private val resourceIdMap: MutableMap<String, Int> = mutableMapOf()

private val String.bindableResourceId: Int get() {
    return bindableResourceClass.let {
        try {
            val field = it.getDeclaredField(this)
            field.getInt(it)
        } catch (e: Throwable) {
            -1
        }
    }
}

fun bindableResourceIdForKProperty(property: KProperty<*>): Int {
    return resourceIdMap.getOrPut(property.name) { property.name.bindableResourceId }
}