package ds.meterscanner.scheduler

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Scheduler @Inject constructor() {

    @Inject lateinit var jobManager: JobManager

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