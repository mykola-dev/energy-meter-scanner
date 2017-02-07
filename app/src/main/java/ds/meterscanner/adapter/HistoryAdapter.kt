package ds.meterscanner.adapter

import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.view.View
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.LazyKodeinAware
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import ds.meterscanner.R
import ds.meterscanner.data.HistoryClickEvent
import ds.meterscanner.data.ItemSelectEvent
import ds.meterscanner.databinding.BindingHolder
import ds.meterscanner.databinding.ViewModelAdapter
import ds.meterscanner.databinding.viewmodel.HistoryItemViewModel
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.util.formatTimeDate
import ds.meterscanner.util.getColorTemp
import org.greenrobot.eventbus.EventBus

class HistoryAdapter : ViewModelAdapter<HistoryItemViewModel, Snapshot>(), LazyKodeinAware {
    override val kodein: LazyKodein = LazyKodein { context.appKodein() }
    val bus: EventBus by instance()

    var isSelectionMode = false
        set(value) {
            if (!value)
                clearSelections()
            notifyDataSetChanged()
            field = value
        }

    override val layoutId: Int = R.layout.item_history

    override fun onFillViewModel(holder: BindingHolder<ViewDataBinding, HistoryItemViewModel>, viewModel: HistoryItemViewModel, item: Snapshot, position: Int) {
        viewModel.value = item.value.toString()
        viewModel.date = formatTimeDate(item.timestamp)
        viewModel.temp = item.outsideTemp?.toString() ?: ""
        viewModel.tempColor = ContextCompat.getColor(context,getColorTemp(item.outsideTemp ?: 0))
        viewModel.onClick = View.OnClickListener {
            if (!isSelectionMode)
                bus.post(HistoryClickEvent(getItem(holder.adapterPosition)))
            else {
                toggleSelection(holder.adapterPosition)
            }
        }
        viewModel.onLongClick = View.OnLongClickListener {
            toggleSelection(holder.adapterPosition)
            true
        }
        viewModel.selectMode = isSelectionMode
        viewModel.checked = item.selected
        viewModel.imageUrl = if (!isSelectionMode) item.image else ""
    }

    private fun toggleSelection(position: Int) {
        val item = getItem(position)
        item.selected = !item.selected
        notifyItemChanged(position)
        bus.post(ItemSelectEvent(getItem(position), getSeledtedItemsCount()))
    }

    private fun clearSelections() {
        getData().forEach { it.selected = false }
    }

    private fun getSeledtedItemsCount() = getData().filter { it.selected }.size


}

