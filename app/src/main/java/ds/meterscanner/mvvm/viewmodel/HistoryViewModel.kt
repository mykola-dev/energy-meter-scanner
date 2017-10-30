package ds.meterscanner.mvvm.viewmodel

import L
import com.github.salomonbrys.kodein.Kodein
import ds.bindingtools.binding
import ds.meterscanner.R
import ds.meterscanner.coroutines.listenValues
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.mvvm.BindableViewModel
import ds.meterscanner.mvvm.Command
import ds.meterscanner.mvvm.ListsView

class HistoryViewModel(kodein: Kodein) : BindableViewModel(kodein) {

    var listItems: List<Snapshot> by binding(emptyList())
    var isActionMode: Boolean by binding()
    var subTitle: String by binding()

    val scrollToPositionCommand = Command<Int>()

    init {
        listenSnapshots()
    }

    private fun listenSnapshots() = async(false) {
        toggleProgress(true)
        val channel = db.getSnapshots().listenValues<Snapshot>()
        for (data in channel) {
            L.d("list updated! size=${data.size}")
            toggleProgress(false)
            listItems = data.reversed()
            subTitle = getString(R.string.items, listItems.size)
        }
    }

    fun onNewSnapshot(view: ListsView) = view.navigateDetails(null)

    fun deleteSelectedItems() = db.deleteSnapshots(listItems.filter { it.selected })

    fun getSeledtedItemsCount() = listItems.filter { it.selected }.size

}
