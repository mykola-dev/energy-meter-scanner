package ds.meterscanner

import L
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import ds.bindingtools.runActivity
import ds.meterscanner.activity.MainActivity
import ds.meterscanner.data.Prefs
import ds.meterscanner.scheduler.Scheduler

class StartupReceiver : BroadcastReceiver(), KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()

    val prefs: Prefs by instance()
    val scheduler: Scheduler by instance()

    override fun onReceive(context: Context, intent: Intent) {
        L.v("BOOT_COMPLETED")
        injector.inject(context.appKodein())
        if (prefs.autostart) {
            scheduler.restoreFromPrefs()
            context.runActivity<MainActivity>(flags = Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    }

}