package ds.bindingtools

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

inline fun <reified T : Any> pref(default: T): PrefDelegate<T> = PrefDelegate(default, T::class)

interface PrefsAware {
    val sharedPreferences: SharedPreferences
    val forcePersistDefaults: Boolean
}

class PrefDelegate<T : Any>(val default: T, val cls: KClass<T>) : ReadWriteProperty<PrefsAware, T> {

    @Suppress("unchecked_cast")
    override fun getValue(thisRef: PrefsAware, property: KProperty<*>): T {
        return thisRef.sharedPreferences.getGeneric(property.name, default, cls)
    }

    override fun setValue(thisRef: PrefsAware, property: KProperty<*>, value: T) {
        thisRef.sharedPreferences.putGeneric(property.name, value)
    }

    operator fun provideDelegate(thisRef: PrefsAware, property: KProperty<*>): ReadWriteProperty<PrefsAware, T> {
        if (thisRef.forcePersistDefaults && !thisRef.sharedPreferences.contains(property.name)) {
            setValue(thisRef, property, default)
            Log.d("prefs", "default value for ${property.name}=$default")
        }
        return this
    }
}


@SuppressLint("CommitPrefEdits")
@Suppress("unchecked_cast")
fun <T : Any> SharedPreferences.putGeneric(key: String, value: T) {
    with(edit()) {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Boolean -> putBoolean(key, value)
            is Set<*> -> putStringSet(key, value as Set<String>)
            else -> throw IllegalArgumentException()
        }
        apply()
    }
}

@Suppress("unchecked_cast")
fun <T : Any> SharedPreferences.getGeneric(key: String, default: T, cls: KClass<T>): T {
    return when (cls) {
        String::class -> getString(key, default as String) as T
        java.lang.Integer::class -> getInt(key, default as Int) as T
        java.lang.Long::class -> getLong(key, default as Long) as T
        java.lang.Float::class -> getFloat(key, default as Float) as T
        java.lang.Boolean::class -> getBoolean(key, default as Boolean) as T
        Set::class -> getStringSet(key, default as Set<String>) as T
        else -> throw IllegalArgumentException()
    }
}

