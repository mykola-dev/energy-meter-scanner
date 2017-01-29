package ds.meterscanner.activity

import android.databinding.ViewDataBinding
import android.os.Bundle
import ds.meterscanner.R
import ds.meterscanner.data.AlarmClickEvent
import ds.meterscanner.data.AlarmDeleteEvent
import ds.meterscanner.databinding.AlarmsView
import ds.meterscanner.databinding.viewmodel.AlarmsViewModel
import ds.meterscanner.ui.DatePickers
import org.greenrobot.eventbus.Subscribe
import java.util.*


class AlarmsActivity : BaseActivity<ViewDataBinding, AlarmsViewModel>(), AlarmsView {

    override fun instantiateViewModel(state: Bundle?): AlarmsViewModel = AlarmsViewModel(this)
    override fun getLayoutId(): Int = R.layout.activity_alarms

    override fun pickTime(time: Date, callback: (hour: Int, minute: Int) -> Unit) {
        DatePickers.pickTime(this, time, callback)
    }

    @Subscribe
    fun onAlarmClickEvent(e: AlarmClickEvent) {
        viewModel.editAlarm(e.jobId)
    }

    @Subscribe
    fun onItemDeleteEvent(e: AlarmDeleteEvent) {
        viewModel.deleteItem(e.jobId)
    }
}
