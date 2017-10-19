package ds.meterscanner.mvvm.view

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutCompat.VERTICAL
import ds.databinding.bind
import ds.databinding.to
import ds.meterscanner.R
import ds.meterscanner.adapter.AlarmsAdapter
import ds.meterscanner.mvvm.AlarmsView
import ds.meterscanner.mvvm.BindableActivity
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.AlarmsViewModel
import ds.meterscanner.ui.DatePickers
import kotlinx.android.synthetic.main.activity_alarms.*
import java.util.*

class AlarmsActivity : BindableActivity<AlarmsViewModel>(), AlarmsView {

    override fun pickTime(time: Date, callback: (hour: Int, minute: Int) -> Unit) =
        DatePickers.pickTime(this, time, callback)

    override fun provideViewModel(): AlarmsViewModel = viewModelOf()
    override fun getLayoutId(): Int = R.layout.activity_alarms

    override fun bindView() {
        super.bindView()
        val adapter = AlarmsAdapter(
            { viewModel.onEditAlarm(this, it) },
            viewModel::onDeleteAlarm
        )
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        viewModel.bind {
            to(::listItems, adapter::data)
            fab.setOnClickListener { onNewAlarm(this@AlarmsActivity) }
        }
    }

}