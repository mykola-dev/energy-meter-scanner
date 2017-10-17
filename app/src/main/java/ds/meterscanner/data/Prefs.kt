/*
 * Copyright (c) 2016. Deviant Studio
 */

package ds.meterscanner.data

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.tasks.Tasks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import ds.bindingtools.PreferencesAware
import ds.bindingtools.pref
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run

class Prefs(ctx: Context, private val remoteConfig: FirebaseRemoteConfig) : PreferencesAware {

    override val forcePersistDefaults = true
    override val sharedPreferences: SharedPreferences = ctx.getSharedPreferences("main_prefs", Context.MODE_PRIVATE)

    var city by pref("Kharkiv")
    var currentTemperature by pref(0.0f)
    var scanTries by pref(1)
    var jpegQuality by pref(50)
    var correctionThreshold by pref(100)
    var shotTimeout by pref(30000)
    var saveImages by pref(true)
    var saveTemperature by pref(true)
    var fixFirstFive by pref(false)
    var autostart by pref(false)
    var boilerTemp by pref(0)

    var viewportX by pref(-1)
    var viewportY by pref(-1)
    var viewportWidth by pref(-1)
    var viewportHeight by pref(-1)

    var alarms by pref<Set<String>>(setOf())

    suspend fun fetchRemote(): Boolean = run(CommonPool) {
        Tasks.await(remoteConfig.fetch())
        remoteConfig.activateFetched()
    }

    suspend fun apiKey(): String = fetchRemoteKey("anyline_api_key")

    suspend private fun fetchRemoteKey(key: String): String {
        var value = remoteConfig.getString(key)
        if (!value.isEmpty())
            return value
        else {
            fetchRemote()
            value = remoteConfig.getString(key)
            return if (!value.isEmpty())
                value
            else
                error("$key key is empty")
        }
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }


}