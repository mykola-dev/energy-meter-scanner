package ds.meterscanner.mvvm.view

import android.databinding.ViewDataBinding
import ds.meterscanner.R
import ds.meterscanner.mvvm.AlarmsView
import ds.meterscanner.mvvm.observe
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.AlarmsViewModel
import ds.meterscanner.ui.DatePickers


class AlarmsActivity : BaseActivity<ViewDataBinding, AlarmsViewModel>(), AlarmsView {

    override fun provideViewModel(): AlarmsViewModel = viewModelOf()
    override fun getLayoutId(): Int = R.layout.activity_alarms

    override fun initViewModel() {
        super.initViewModel()
        viewModel.pickTimeCommand.observe(this) { params ->
            DatePickers.pickTime(this, params.time, params.callback)
        }
    }

}
