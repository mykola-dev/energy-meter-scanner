package ds.meterscanner.mvvm.viewmodel

import android.databinding.ObservableField
import com.evernote.android.job.JobRequest
import com.evernote.android.job.scheduledTo
import ds.meterscanner.R
import ds.meterscanner.adapter.AlarmsAdapter
import ds.meterscanner.mvvm.BaseViewModel
import ds.meterscanner.mvvm.PickTimeCommand
import ds.meterscanner.mvvm.ViewModelAdapter
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmsViewModel : BaseViewModel() {

    val listItems = ObservableField<List<JobRequest>>()

    val pickTimeCommand = PickTimeCommand()

    init {
        toolbar.title = getString(R.string.alarms)
        fillAdapter()
    }

    private fun fillAdapter() {
        val alarms = scheduler.getScheduledJobs().sortedBy { it.scheduledTo() }
        listItems.set(alarms)
    }

    fun onNewAlarm() {
        val time = Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))
        pickTimeCommand(time) { hours, minutes ->
            scheduler.scheduleSnapshotJob(hours, minutes)
            scheduler.saveToPrefs()
            fillAdapter()
        }
    }

    private fun onEditAlarm(jobId: Int) {
        val time = Date(scheduler.getJob(jobId)?.scheduledTo() ?: System.currentTimeMillis())
        pickTimeCommand(time) { hours, minutes ->
            scheduler.clearJob(jobId)
            scheduler.scheduleSnapshotJob(hours, minutes)
            scheduler.saveToPrefs()
            fillAdapter()
        }
    }

    private fun onDeleteAlarm(jobId: Int) {
        scheduler.clearJob(jobId)
        scheduler.saveToPrefs()
        fillAdapter()
    }

    val adapter: ViewModelAdapter<*, *> get() = AlarmsAdapter(::onEditAlarm, ::onDeleteAlarm)

}

