package ds.meterscanner.scheduler

import L
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.evernote.android.job.rescheduled
import com.evernote.android.job.scheduledTo
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.erased.instance
import ds.meterscanner.data.Prefs
import ds.meterscanner.util.formatTimeDate
import java.util.*
import java.util.concurrent.TimeUnit

class Scheduler(override val kodein: Kodein) : KodeinAware {

    private val jobManager: JobManager = instance()
    private val prefs: Prefs = instance()

    fun getScheduledJobs(): MutableSet<JobRequest> = jobManager.allJobRequests

    fun clearAllJobs() {
        jobManager.cancelAll()
    }

    fun clearJob(id: Int) {
        jobManager.cancel(id)
    }

    fun getJob(id: Int): JobRequest? = jobManager.getJobRequest(id)

    fun saveToPrefs() {
        val set = getScheduledJobs()
            .filter { !it.rescheduled }
            .map(JobRequest::scheduledTo)
            .map(Long::toString)
            .toSet()
        prefs.alarms = set
    }

    fun restoreFromPrefs() {
        clearAllJobs()
        val alarmsSet = prefs.alarms
        alarmsSet
            .map(String::toLong)
            .forEach {
                val curr: Calendar = instance()
                val cal: Calendar = instance()
                cal.timeInMillis = it
                while (cal.before(curr)) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                scheduleSnapshotJob(cal.timeInMillis - curr.timeInMillis)
            }
    }

    fun scheduleSnapshotJob(hours: Int, minutes: Int) {
        val cal: Calendar = instance()
        cal.set(Calendar.HOUR_OF_DAY, hours)
        cal.set(Calendar.MINUTE, minutes)
        val curr = Calendar.getInstance()
        if (cal.before(curr))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        val diff = cal.timeInMillis - curr.timeInMillis
        scheduleSnapshotJob(diff)

    }

    fun scheduleSnapshotJob(delay: Long) {
        L.v("scheduler: schedule next task ${formatTimeDate(System.currentTimeMillis() + delay)}")
        JobRequest.Builder(SnapshotJob::class.java.name)
            //.setPersisted(true)
            .setExact(delay)
            .setBackoffCriteria(TimeUnit.MINUTES.toMillis(2), JobRequest.BackoffPolicy.LINEAR)
            .build()
            .schedule()
    }

    // reschedule in one day
    fun reschedule() {
        scheduleSnapshotJob(TimeUnit.DAYS.toMillis(1))
    }


}