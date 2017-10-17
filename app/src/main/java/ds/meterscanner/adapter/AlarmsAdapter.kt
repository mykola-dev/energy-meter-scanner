package ds.meterscanner.adapter

import android.databinding.ViewDataBinding
import android.view.View
import com.evernote.android.job.JobRequest
import com.evernote.android.job.rescheduled
import com.evernote.android.job.scheduledTo
import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.erased.instance
import ds.meterscanner.R
import ds.meterscanner.data.AlarmClickEvent
import ds.meterscanner.data.AlarmDeleteEvent
import ds.meterscanner.mvvm.BindingHolder
import ds.meterscanner.mvvm.ViewModelAdapter
import ds.meterscanner.mvvm.viewmodel.AlarmItemViewModel
import ds.meterscanner.util.formatTime
import org.greenrobot.eventbus.EventBus

class AlarmsAdapter : ViewModelAdapter<AlarmItemViewModel, JobRequest>(), KodeinGlobalAware {

    val bus: EventBus = instance()

    override val layoutId: Int = R.layout.item_alarm

    override fun onFillViewModel(holder: BindingHolder<ViewDataBinding, AlarmItemViewModel>, viewModel: AlarmItemViewModel, item: JobRequest, position: Int) {
        viewModel.time = formatTime(item.scheduledTo())
        viewModel.onClick = View.OnClickListener { bus.post(AlarmClickEvent(item.jobId)) }
        viewModel.onDeleteClick = View.OnClickListener { bus.post(AlarmDeleteEvent(item.jobId)) }
        viewModel.rescheduled = item.rescheduled
    }

}

