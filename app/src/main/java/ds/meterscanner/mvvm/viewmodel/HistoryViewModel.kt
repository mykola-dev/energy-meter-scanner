package ds.meterscanner.mvvm.viewmodel

import L
import android.databinding.ObservableField
import ds.meterscanner.R
import ds.meterscanner.adapter.HistoryAdapter
import ds.meterscanner.coroutines.listenValues
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.mvvm.BaseViewModel
import ds.meterscanner.mvvm.Command
import ds.meterscanner.mvvm.ListsView

class HistoryViewModel : BaseViewModel() {

    val adapter = ObservableField<HistoryAdapter>()

    val scrollToPositionCommand = Command<Int>()

    init {
        toolbar.title = getString(R.string.history)
        adapter.set(HistoryAdapter())
        listenSnapshots()
    }

    private fun listenSnapshots() = async {
        try {
            toggleProgress(true)
            val channel = db.getSnapshots().listenValues<Snapshot>(context)
            for (data in channel) {
                L.d("list updated! size=${data.size}")
                toggleProgress(false)
                adapter.get().setData(data)
                scrollToPositionCommand(data.size - 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onErrorSnack(e)
        }

    }

    fun onNewSnapshot(view: ListsView) {
        view.runDetails(null)
    }

    fun toggleSelectionMode(enable: Boolean) {
        adapter.get().isSelectionMode = enable
    }

    fun deleteSelectedItems() {
        db.deleteSnapshots(adapter.get().getData().filter { it.selected })
    }

}

