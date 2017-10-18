package ds.meterscanner.adapter

import android.databinding.ViewDataBinding
import com.evernote.android.job.JobRequest
import com.evernote.android.job.rescheduled
import com.evernote.android.job.scheduledTo
import ds.meterscanner.R
import ds.meterscanner.mvvm.BindingHolder
import ds.meterscanner.mvvm.ViewModelAdapter
import ds.meterscanner.mvvm.viewmodel.AlarmItemViewModel
import ds.meterscanner.util.formatTime

class AlarmsAdapter(
    private val onEditAlarm: (jobId: Int) -> Unit,
    private val onDeleteAlarm: (jobId: Int) -> Unit
) : ViewModelAdapter<AlarmItemViewModel, JobRequest>() {

    override val layoutId: Int = R.layout.item_alarm

    override fun onFillViewModel(holder: BindingHolder<ViewDataBinding, AlarmItemViewModel>, viewModel: AlarmItemViewModel, item: JobRequest, position: Int) {
        viewModel.jobId = item.jobId
        viewModel.time = formatTime(item.scheduledTo())
        viewModel.onClickDelegate = onEditAlarm
        viewModel.onDeleteClickDelegate = onDeleteAlarm
        viewModel.rescheduled = item.rescheduled
    }
}