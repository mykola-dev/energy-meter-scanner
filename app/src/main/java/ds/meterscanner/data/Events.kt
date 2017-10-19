package ds.meterscanner.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.annotation.StringDef
import android.support.v4.content.LocalBroadcastManager

@Retention(AnnotationRetention.SOURCE)
@StringDef(INTERRUPT_EVENT)
annotation class EventDef

const val INTERRUPT_EVENT = "INTERRUPT_EVENT"

fun Context.sendEvent(@EventDef action: String) = LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(action))

fun Context.subscribeEvent(@EventDef action: String, callback: EventCallback) =
    LocalBroadcastManager.getInstance(this).registerReceiver(callback.receiver, IntentFilter(action))

fun Context.unsubscribeEvent(callback: EventCallback) =
    LocalBroadcastManager.getInstance(this).unregisterReceiver(callback.receiver)

class EventCallback(
    val callback: () -> Unit
) {
    val receiver: BroadcastReceiver

    init {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                callback()
            }
        }
    }
}
