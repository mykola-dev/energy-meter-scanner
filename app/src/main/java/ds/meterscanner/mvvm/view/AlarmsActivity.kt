package ds.meterscanner.mvvm.view

import android.databinding.ViewDataBinding
import ds.meterscanner.R
import ds.meterscanner.adapter.AlarmsAdapter
import ds.meterscanner.mvvm.AlarmsView
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.AlarmsViewModel
import ds.meterscanner.ui.DatePickers
import java.util.*

class AlarmsActivity : BaseActivity<ViewDataBinding, AlarmsViewModel>(), AlarmsView {

    override val adapter: AlarmsAdapter
        get() = AlarmsAdapter(
            { viewModel.onEditAlarm(this, it) },
            viewModel::onDeleteAlarm
        )

    override fun pickTime(time: Date, callback: (hour: Int, minute: Int) -> Unit) =
        DatePickers.pickTime(this, time, callback)

    override fun provideViewModel(): AlarmsViewModel = viewModelOf()
    override fun getLayoutId(): Int = R.layout.activity_alarms

}