package ds.meterscanner.mvvm.viewmodel

import android.databinding.ObservableField
import com.evernote.android.job.JobRequest
import com.evernote.android.job.scheduledTo
import ds.meterscanner.R
import ds.meterscanner.mvvm.AlarmsView
import ds.meterscanner.mvvm.BaseViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmsViewModel : BaseViewModel() {

    val listItems = ObservableField<List<JobRequest>>()

    init {
        toolbar.title = getString(R.string.alarms)
        fillAdapter()
    }

    private fun fillAdapter() {
        val alarms = scheduler.getScheduledJobs().sortedBy { it.scheduledTo() }
        listItems.set(alarms)
    }

    fun onNewAlarm(view:AlarmsView) {
        val time = Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))
        view.pickTime(time) { hours, minutes ->
            scheduler.scheduleSnapshotJob(hours, minutes)
            scheduler.saveToPrefs()
            fillAdapter()
        }
    }

    fun onEditAlarm(view:AlarmsView, jobId: Int) {
        val time = Date(scheduler.getJob(jobId)?.scheduledTo() ?: System.currentTimeMillis())
        view.pickTime(time) { hours, minutes ->
            scheduler.clearJob(jobId)
            scheduler.scheduleSnapshotJob(hours, minutes)
            scheduler.saveToPrefs()
            fillAdapter()
        }
    }

    fun onDeleteAlarm(jobId: Int) {
        scheduler.clearJob(jobId)
        scheduler.saveToPrefs()
        fillAdapter()
    }

}

