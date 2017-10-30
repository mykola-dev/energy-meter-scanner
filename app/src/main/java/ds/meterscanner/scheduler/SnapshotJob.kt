package ds.meterscanner.scheduler

import L
import android.content.Context
import android.content.Intent
import com.evernote.android.job.Job
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import ds.bindingtools.startActivity
import ds.meterscanner.coroutines.Locker
import ds.meterscanner.data.INTERRUPT_EVENT
import ds.meterscanner.data.Prefs
import ds.meterscanner.data.sendEvent
import ds.meterscanner.db.FirebaseDb
import ds.meterscanner.mvvm.view.MainActivity
import ds.meterscanner.net.NetLayer
import ds.meterscanner.util.ThreadTools
import ds.meterscanner.util.formatTimeDate
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withTimeout
import java.util.*


private const val JOB_RETRIES = 3

class SnapshotJob : Job(), KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()

    private val prefs: Prefs by instance()
    private val restService: NetLayer by instance()
    private val db: FirebaseDb by instance()
    private val scheduler: Scheduler by instance()

    override fun onRunJob(params: Job.Params): Job.Result {
        injector.inject(context.appKodein())
        val success = doJob(context, params.id)

        if (params.failureCount == 0) {
            if (params.scheduledAt != 0L) {
                val cal: Calendar by instance()
                cal.timeInMillis = params.scheduledAt + params.startMs
                val curr: Calendar by instance()
                while (cal.before(curr)) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                scheduler.scheduleSnapshotJob(cal.timeInMillis - curr.timeInMillis)

            } else {
                scheduler.reschedule()
            }
        }

        val result = when {
            success -> Job.Result.SUCCESS
            params.failureCount < JOB_RETRIES -> {
                L.e("job failures=${params.failureCount + 1}")
                Job.Result.RESCHEDULE
            }
            else -> Job.Result.FAILURE
        }

        db.log("job status: id=${params.id} start=${params.startMs / 1000}  backoff${params.backoffMs / 1000} policy=${params.backoffPolicy} tag=${params.tag} " +
            "end=${params.endMs / 1000} failures=${params.failureCount} isExact=${params.isExact} ${formatTimeDate(params.scheduledAt)}")

        return result
    }

    private fun doJob(context: Context, jobId: Int): Boolean = runBlocking {
        if (ThreadTools.isUiThread)
            error("Main Thread detected")

        L.v("scheduler: start the job")

        val weather: Double = if (prefs.saveTemperature) {
            try {
                withTimeout(10_000) {
                    restService.getWeather().main.temp
                }
            } catch (e: Exception) {
                prefs.currentTemperature.toDouble()
            }

        } else 0.0
        prefs.currentTemperature = weather.toFloat()
        L.v("fetched weather, t=$weather")
        launch(UI) {
            context.applicationContext.startActivity<MainActivity>(flags = Intent.FLAG_ACTIVITY_NEW_TASK) {
                MainActivity::jobId to jobId
            }
        }

        if (!Locker.lock(jobId, prefs.shotTimeout.toLong())) {
            L.w("threads: interrupted by timeout")
            context.sendEvent(INTERRUPT_EVENT)
            return@runBlocking false
        }

        L.v("scheduler: task was successful")
        return@runBlocking true
    }

}
