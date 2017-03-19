package ds.meterscanner.databinding.viewmodel

import L
import android.databinding.ObservableField
import ds.meterscanner.R
import ds.meterscanner.adapter.HistoryAdapter
import ds.meterscanner.coroutines.listenValues
import ds.meterscanner.databinding.BaseViewModel
import ds.meterscanner.databinding.ListsView
import ds.meterscanner.db.model.Snapshot

class HistoryViewModel(view: ListsView) : BaseViewModel<ListsView>(view) {

    val adapter = ObservableField<HistoryAdapter>()

    override fun onCreate() {
        super.onCreate()
        toolbar.title.set(view.getString(R.string.history))
        adapter.set(HistoryAdapter())
    }

    override fun onAttach() {
        super.onAttach()
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
                view.scrollToPosition(data.size - 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onErrorSnack(e)
        }

    }

    fun onNewSnapshot() {
        view.runDetails(null)
    }

    fun toggleSelectionMode(enable: Boolean) {
        adapter.get().isSelectionMode = enable
    }

    fun deleteSelectedItems() {
        db.deleteSnapshots(adapter.get().getData().filter { it.selected })
    }

}

