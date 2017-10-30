package ds.meterscanner.mvvm.view

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutCompat.VERTICAL
import ds.bindingtools.withBindable
import ds.meterscanner.R
import ds.meterscanner.adapter.AlarmsAdapter
import ds.meterscanner.mvvm.AlarmsView
import ds.meterscanner.mvvm.BindableActivity
import ds.meterscanner.mvvm.viewmodel.AlarmsViewModel
import ds.meterscanner.ui.DatePickers
import kotlinx.android.synthetic.main.activity_alarms.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class AlarmsActivity : BindableActivity<AlarmsViewModel>(), AlarmsView {

    override fun pickTime(time: Date, callback: (hour: Int, minute: Int) -> Unit) =
        DatePickers.pickTime(this, time, callback)

    override fun provideViewModel(): AlarmsViewModel = defaultViewModelOf()
    override fun getLayoutId(): Int = R.layout.activity_alarms

    override fun bindView() {
        super.bindView()
        toolbar.title = getString(R.string.alarms)
        val adapter = AlarmsAdapter(
            { viewModel.onEditAlarm(this, it) },
            viewModel::onDeleteAlarm
        )
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        fab.setOnClickListener { viewModel.onNewAlarm(this) }

        withBindable(viewModel) {
            bind(::listItems, adapter::data)
        }
    }

}