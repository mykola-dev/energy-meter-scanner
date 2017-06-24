package ds.meterscanner.mvvm.activity

import android.databinding.ViewDataBinding
import ds.meterscanner.R
import ds.meterscanner.data.AlarmClickEvent
import ds.meterscanner.data.AlarmDeleteEvent
import ds.meterscanner.mvvm.AlarmsView
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.AlarmsViewModel
import ds.meterscanner.ui.DatePickers
import org.greenrobot.eventbus.Subscribe
import java.util.*


class AlarmsActivity : BaseActivity3<ViewDataBinding, AlarmsViewModel>(), AlarmsView {

    override fun provideViewModel(): AlarmsViewModel = viewModelOf()
    override fun getLayoutId(): Int = R.layout.activity_alarms

    override fun pickTime(time: Date, callback: (hour: Int, minute: Int) -> Unit) {
        DatePickers.pickTime(this, time, callback)
    }

    @Subscribe
    fun onAlarmClickEvent(e: AlarmClickEvent) = viewModel.onEditAlarm(this, e.jobId)

    @Subscribe
    fun onItemDeleteEvent(e: AlarmDeleteEvent) = viewModel.onDeleteAlarm(e.jobId)
}
