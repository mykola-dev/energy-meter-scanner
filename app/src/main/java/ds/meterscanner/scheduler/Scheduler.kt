package ds.meterscanner.scheduler

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.instance
import java.util.concurrent.TimeUnit

class Scheduler(override val kodein: Kodein) : KodeinAware {

    private val jobManager: JobManager = instance()

    fun addDefaultTasks() = scheduleSnapshotJob(TimeUnit.MINUTES.toMillis(2))   // 2 minutes

    fun addJob(hours: Int, minutes: Int) = scheduleSnapshotJob(hours, minutes)

    fun getScheduledJobs(): MutableSet<JobRequest> = jobManager.allJobRequests

    fun clearAllJobs() {
        jobManager.cancelAll()
    }

    fun clearJob(id: Int) {
        jobManager.cancel(id)
    }

    fun getJob(id: Int): JobRequest? = jobManager.getJobRequest(id)


}