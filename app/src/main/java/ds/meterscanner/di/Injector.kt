/*
 * Copyright (c) 2016. Deviant Studio
 */

package ds.meterscanner.di

import ds.meterscanner.App
import kotlin.properties.Delegates


var mainComponent: MainComponent by Delegates.notNull()

fun initDagger(app: App) {
    mainComponent = DaggerMainComponent
        .builder()
        .appModule(AppModule(app))
        .build()

}
