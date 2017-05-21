package ds.meterscanner.databinding.viewmodel

import android.databinding.ObservableField
import com.evernote.android.job.scheduledTo
import ds.meterscanner.R
import ds.meterscanner.adapter.AlarmsAdapter
import ds.meterscanner.databinding.AlarmsView
import ds.meterscanner.databinding.BaseViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmsViewModel(view: AlarmsView) : BaseViewModel<AlarmsView>(view) {

    val adapter = ObservableField<AlarmsAdapter>()

    override fun onCreate() {
        super.onCreate()
        toolbar.title = view.getString(R.string.alarms)
        adapter.set(AlarmsAdapter())
    }

    override fun onAttach() {
        super.onAttach()
        fillAdapter()
    }

    private fun fillAdapter() {
        val data = scheduler.getScheduledJobs().toList().sortedBy { it.scheduledTo() }
        adapter.get().setData(data)
    }

    fun onNewAlarm() {
        view.pickTime(Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)), { hours, minutes ->
            scheduler.scheduleSnapshotJob(hours, minutes)
            scheduler.saveToPrefs()
            fillAdapter()
        })
    }

    fun deleteItem(id: Int) {
        scheduler.clearJob(id)
        scheduler.saveToPrefs()
        fillAdapter()
    }

    fun editAlarm(id: Int) {
        val date = Date(scheduler.getJob(id)?.scheduledTo() ?: System.currentTimeMillis())
        view.pickTime(date, { hours, minutes ->
            scheduler.clearJob(id)
            scheduler.scheduleSnapshotJob(hours, minutes)
            scheduler.saveToPrefs()
            fillAdapter()
        })
    }

}

