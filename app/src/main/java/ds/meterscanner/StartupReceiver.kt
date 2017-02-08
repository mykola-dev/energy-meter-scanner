package ds.meterscanner

import L
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ds.bindingtools.runActivity
import ds.meterscanner.activity.MainActivity
import ds.meterscanner.data.Prefs
import ds.meterscanner.di.mainComponent
import javax.inject.Inject

class StartupReceiver : BroadcastReceiver() {

    @Inject lateinit var prefs: Prefs

    init {
        mainComponent.inject(this)
    }
    override fun onReceive(context: Context, intent: Intent) {
        L.v("BOOT_COMPLETED")
        if (prefs.autostart) {
            context.runActivity<MainActivity>(flags = Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    }

}