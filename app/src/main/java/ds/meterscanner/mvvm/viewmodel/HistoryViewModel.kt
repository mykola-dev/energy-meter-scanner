package ds.meterscanner.mvvm.viewmodel

import L
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import ds.meterscanner.R
import ds.meterscanner.coroutines.listenValues
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.mvvm.BaseViewModel
import ds.meterscanner.mvvm.Command
import ds.meterscanner.mvvm.ListsView

class HistoryViewModel : BaseViewModel() {

    val listItems = ObservableField<List<Snapshot>>()

    val isActionMode = MutableLiveData<Boolean>()
    val scrollToPositionCommand = Command<Int>()

    init {
        toolbar.title = getString(R.string.history)
        listenSnapshots()
    }

    private fun listenSnapshots() = async {
        try {
            toggleProgress(true)
            val channel = db.getSnapshots().listenValues<Snapshot>()
            for (data in channel) {
                L.d("list updated! size=${data.size}")
                toggleProgress(false)
                listItems.set(data)
                scrollToPositionCommand(data.size - 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onErrorSnack(e)
        }
    }

    fun onNewSnapshot(view: ListsView) = view.navigateDetails(null)

    fun deleteSelectedItems() = db.deleteSnapshots(listItems.get().filter { it.selected })

    fun getSeledtedItemsCount() = listItems.get()?.filter { it.selected }?.size ?: 0

}
