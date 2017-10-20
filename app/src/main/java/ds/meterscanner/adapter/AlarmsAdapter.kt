package ds.meterscanner.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.evernote.android.job.JobRequest
import com.evernote.android.job.rescheduled
import com.evernote.android.job.scheduledTo
import ds.meterscanner.R
import ds.meterscanner.mvvm.SimpleAdapter
import ds.meterscanner.util.formatTime
import ds.meterscanner.util.visible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_alarm.*

class AlarmsAdapter(
    private val onEditAlarm: (jobId: Int) -> Unit,
    private val onDeleteAlarm: (jobId: Int) -> Unit
) : SimpleAdapter<AlarmViewHolder, JobRequest>() {

    override val layoutId: Int = R.layout.item_alarm

    override fun onFillView(holder: AlarmViewHolder, item: JobRequest, position: Int) = with(holder) {
        time.text = formatTime(item.scheduledTo())
        containerView.setOnClickListener { onEditAlarm(item.jobId) }
        delete.setOnClickListener { onDeleteAlarm(item.jobId) }
        time.isEnabled = !item.rescheduled
        containerView.isEnabled = !item.rescheduled
        rescheduled.visible = item.rescheduled
    }

}

class AlarmViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    val time = titleLabel
    val rescheduled = rescheduledLabel
    val delete = deleteButton
}