package ds.meterscanner.databinding.viewmodel

import L
import android.databinding.ObservableField
import ds.meterscanner.R
import ds.meterscanner.adapter.HistoryAdapter
import ds.meterscanner.databinding.BaseViewModel
import ds.meterscanner.databinding.ListsView
import ds.meterscanner.rx.onErrorSnackbar

class HistoryViewModel(view: ListsView) : BaseViewModel<ListsView>(view) {

    val adapter = ObservableField<HistoryAdapter>()

    override fun onCreate() {
        super.onCreate()
        toolbar.title.set(view.getString(R.string.history))
        adapter.set(HistoryAdapter())
    }

    override fun onAttach() {
        super.onAttach()
        db.listenSnapshots()
            .doOnSubscribe { toggleProgress(true) }
            .doOnNext { L.v("new values arrived!") }
            .doOnNext { toggleProgress(false) }
            .bindTo(ViewModelEvent.DETACH)
            .subscribe({
                adapter.get().setData(it)
                view.scrollToPosition(it.size - 1)
            }, onErrorSnackbar(view))
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

