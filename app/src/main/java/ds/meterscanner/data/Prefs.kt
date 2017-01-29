/*
 * Copyright (c) 2016. Deviant Studio
 */

package ds.meterscanner.data

import L
import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.tasks.Tasks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import ds.bindingtools.PrefsAware
import ds.bindingtools.pref
import ds.meterscanner.rx.applySchedulers
import io.reactivex.Single

class Prefs(ctx: Context, private val remoteConfig: FirebaseRemoteConfig) : PrefsAware {

    override val forcePersistDefaults = true
    override val sharedPreferences: SharedPreferences = ctx.getSharedPreferences("main_prefs", Context.MODE_PRIVATE)

    init {
        L.i("::: Prefs initialized")
    }

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

    fun fetchRemote(): Single<Boolean> = Single.fromCallable {
        Tasks.await(remoteConfig.fetch())
        remoteConfig.activateFetched()
    }.applySchedulers()

    fun apiKey(): Single<String> = fetchRemoteKey("anyline_api_key")

    private fun fetchRemoteKey(key: String): Single<String> {
        var value = remoteConfig.getString(key)
        if (!value.isEmpty())
            return Single.just(value)
        else
            return fetchRemote().map {
                value = remoteConfig.getString(key)
                if (!value.isEmpty())
                    value
                else
                    error("$key key is empty")
            }
    }


}