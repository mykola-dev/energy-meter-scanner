package ds.meterscanner.adapter

import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.view.View
import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import ds.meterscanner.R
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.mvvm.BindingHolder
import ds.meterscanner.mvvm.ViewModelAdapter
import ds.meterscanner.mvvm.viewmodel.HistoryItemViewModel
import ds.meterscanner.util.formatTimeDate
import ds.meterscanner.util.getColorTemp

class HistoryAdapter(
    isActionMode: Boolean,
    private val onItemClick: (Snapshot) -> Unit,
    private val onToggleSelection: () -> Unit
) : ViewModelAdapter<HistoryItemViewModel, Snapshot>(), KodeinGlobalAware {

    var isActionMode = isActionMode
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
        viewModel.tempColor = ContextCompat.getColor(context, getColorTemp(item.outsideTemp ?: 0))
        viewModel.onClick = View.OnClickListener {
            if (!isActionMode)
                onItemClick(getItem(holder.adapterPosition))
            else {
                toggleSelection(holder.adapterPosition)
            }
        }
        viewModel.onLongClick = View.OnLongClickListener {
            toggleSelection(holder.adapterPosition)
            true
        }
        viewModel.selectMode = isActionMode
        viewModel.checked = item.selected
        viewModel.imageUrl = if (!isActionMode) item.image else ""
    }

    private fun toggleSelection(position: Int) {
        val item = getItem(position)
        item.selected = !item.selected
        notifyItemChanged(position)
        onToggleSelection()
    }

    private fun clearSelections() {
        getData().forEach { it.selected = false }
    }

}
