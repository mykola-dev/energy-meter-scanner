package ds.meterscanner.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.erased.instance
import ds.meterscanner.R
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.mvvm.SimpleAdapter
import ds.meterscanner.util.formatTimeDate
import ds.meterscanner.util.getColorTemp
import ds.meterscanner.util.visible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_history.*

class HistoryAdapter(
    private val onItemClick: (Snapshot) -> Unit,
    private val onToggleSelection: () -> Unit
) : SimpleAdapter<HistoryViewHolder, Snapshot>(), KodeinGlobalAware {

    val glide: RequestManager = instance()

    var isActionMode = false
        set(value) {
            if (!value)
                clearSelections()
            notifyDataSetChanged()
            field = value
        }

    override val layoutId: Int = R.layout.item_history

    override fun onFillView(holder: HistoryViewHolder, item: Snapshot, position: Int) = with(holder) {
        value.text = item.value.toString()
        date.text = formatTimeDate(item.timestamp)
        temperature.text = item.outsideTemp?.toString() ?: ""
        temperature.setTextColor(ContextCompat.getColor(context, getColorTemp(item.outsideTemp ?: 0)))
        containerView.setOnClickListener {
            if (!isActionMode)
                onItemClick(getItem(holder.adapterPosition))
            else {
                toggleSelection(holder.adapterPosition)
            }
        }
        containerView.setOnLongClickListener {
            toggleSelection(holder.adapterPosition)
            true
        }
        checkBox.visible = isActionMode
        checkBox.isChecked = item.selected
        glide
            .load(if (!isActionMode) item.image else "")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    private fun toggleSelection(position: Int) {
        val item = getItem(position)
        item.selected = !item.selected
        notifyItemChanged(position)
        onToggleSelection()
    }

    private fun clearSelections() {
        data.forEach { it.selected = false }
    }

}

class HistoryViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    val value = titleLabel
    val date = dateLabel
    val image = imageView
    val temperature = temperatureLabel
    val checkBox = selectedCheck
}