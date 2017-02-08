package ds.meterscanner.adapter

import android.databinding.ViewDataBinding
import android.view.View
import com.evernote.android.job.JobRequest
import com.evernote.android.job.rescheduled
import com.evernote.android.job.scheduledTo
import ds.meterscanner.R
import ds.meterscanner.data.AlarmClickEvent
import ds.meterscanner.data.AlarmDeleteEvent
import ds.meterscanner.databinding.BindingHolder
import ds.meterscanner.databinding.ViewModelAdapter
import ds.meterscanner.databinding.viewmodel.AlarmItemViewModel
import ds.meterscanner.util.formatTime
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class AlarmsAdapter : ViewModelAdapter<AlarmItemViewModel, JobRequest>() {

    @Inject lateinit var  bus: EventBus

    override val layoutId: Int = R.layout.item_alarm

    override fun onFillViewModel(holder: BindingHolder<ViewDataBinding, AlarmItemViewModel>, viewModel: AlarmItemViewModel, item: JobRequest, position: Int) {
        viewModel.time = formatTime(item.scheduledTo())
        viewModel.onClick = View.OnClickListener { bus.post(AlarmClickEvent(item.jobId)) }
        viewModel.onDeleteClick = View.OnClickListener { bus.post(AlarmDeleteEvent(item.jobId)) }
        viewModel.rescheduled = item.rescheduled
    }


}

