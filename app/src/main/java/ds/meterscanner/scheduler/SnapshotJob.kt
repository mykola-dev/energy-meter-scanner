package ds.meterscanner.scheduler

import L
import android.content.Context
import android.content.Intent
import com.evernote.android.job.Job
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.erased.instance
import ds.bindingtools.runActivity
import ds.meterscanner.activity.MainActivity
import ds.meterscanner.data.InterruptEvent
import ds.meterscanner.data.Prefs
import ds.meterscanner.db.FirebaseDb
import ds.meterscanner.net.NetLayer
import ds.meterscanner.util.ThreadTools
import ds.meterscanner.util.formatTimeDate
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit


class SnapshotJob : Job(), KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()

    val JOB_RETRIES = 3

    val prefs: Prefs by instance()
    val bus: EventBus by instance()
    val restService: NetLayer by instance()
    val db: FirebaseDb by instance()
    val scheduler: Scheduler by instance()

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

        val result = if (success) {
            Job.Result.SUCCESS
        } else if (params.failureCount < JOB_RETRIES) {
            L.e("job failures=${params.failureCount + 1}")
            Job.Result.RESCHEDULE
        } else
            Job.Result.FAILURE

        db.log("job status: id=${params.id} start=${params.startMs / 1000}  backoff${params.backoffMs / 1000} policy=${params.backoffPolicy} tag=${params.tag} " +
            "end=${params.endMs / 1000} failures=${params.failureCount} isExact=${params.isExact} ${formatTimeDate(params.scheduledAt)}")

        return result
    }

    fun doJob(context: Context, jobId: Int): Boolean {
        if (ThreadTools.isUiThread)
            error("Main Thread detected")

        L.v("scheduler: start the job")
        (
            if (prefs.saveTemperature)
                restService
                    .getWeather()
                    .map { it.main.temp }
                    .timeout(10, TimeUnit.SECONDS)
            else
                Observable.just(0.0)
            )
            .doOnNext { prefs.currentTemperature = it.toFloat() }
            .doOnNext { L.v("fetched weather, t=$it") }
            .onErrorReturn { prefs.currentTemperature.toDouble() }
            .observeOn(mainThread())
            .subscribe {
                context.applicationContext.runActivity<MainActivity>(flags = Intent.FLAG_ACTIVITY_NEW_TASK) {
                    MainActivity::jobId..jobId
                }

            }

        try {
            if (!ThreadTools.lock(jobId, prefs.shotTimeout.toLong())) {
                L.w("threads: interrupted by timeout")
                bus.post(InterruptEvent())
                return false
            }
        } catch (ignored: InterruptedException) {
        }

        L.v("scheduler: task was successful")
        return true
    }

}
