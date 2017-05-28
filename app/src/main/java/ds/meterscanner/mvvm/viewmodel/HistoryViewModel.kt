package ds.meterscanner.mvvm.viewmodel

import L
import android.databinding.Bindable
import ds.bindingtools.observableField
import ds.meterscanner.R
import ds.meterscanner.adapter.HistoryAdapter
import ds.meterscanner.coroutines.listenValues
import ds.meterscanner.mvvm.BaseViewModel
import ds.meterscanner.mvvm.ListsView
import ds.meterscanner.db.model.Snapshot

class HistoryViewModel(view: ListsView) : BaseViewModel<ListsView>(view) {

    @get:Bindable var adapter by observableField<HistoryAdapter>()

    override fun onCreate() {
        super.onCreate()
        toolbar.title = view.getString(R.string.history)
        adapter = HistoryAdapter()
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
                adapter?.setData(data)
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
        adapter?.isSelectionMode = enable
    }

    fun deleteSelectedItems() {
        db.deleteSnapshots(adapter!!.getData().filter { it.selected })
    }

}

