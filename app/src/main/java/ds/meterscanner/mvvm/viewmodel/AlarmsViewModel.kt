package ds.meterscanner.mvvm.viewmodel

import android.app.Application
import android.databinding.BaseObservable
import android.databinding.Observable
import android.databinding.ObservableField
import com.evernote.android.job.scheduledTo
import ds.meterscanner.R
import ds.meterscanner.adapter.AlarmsAdapter
import ds.meterscanner.mvvm.AlarmsView
import ds.meterscanner.mvvm.BaseViewModel3
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmsViewModel(app: Application) : BaseViewModel3(app), Observable by BaseObservable() {

    val adapter = ObservableField<AlarmsAdapter>()

    init {
        toolbar.title = getString(R.string.alarms)
        adapter.set(AlarmsAdapter())
        fillAdapter()
    }

    private fun fillAdapter() {
        val data = scheduler.getScheduledJobs().toList().sortedBy { it.scheduledTo() }
        adapter.get().setData(data)
    }

    fun onNewAlarm(view: AlarmsView) {
        view.pickTime(Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))) { hours, minutes ->
            scheduler.scheduleSnapshotJob(hours, minutes)
            scheduler.saveToPrefs()
            fillAdapter()
        }
    }

    fun onEditAlarm(view: AlarmsView, jobId: Int) {
        val date = Date(scheduler.getJob(jobId)?.scheduledTo() ?: System.currentTimeMillis())
        view.pickTime(date) { hours, minutes ->
            scheduler.clearJob(jobId)
            scheduler.scheduleSnapshotJob(hours, minutes)
            scheduler.saveToPrefs()
            fillAdapter()
        }

    }

    fun onDeleteAlarm(id: Int) {
        scheduler.clearJob(id)
        scheduler.saveToPrefs()
        fillAdapter()
    }

}

