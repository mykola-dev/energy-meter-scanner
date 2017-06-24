package ds.meterscanner.mvvm.view

import L
import android.support.v7.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import ds.bindingtools.runActivity
import ds.meterscanner.R
import ds.meterscanner.data.HistoryClickEvent
import ds.meterscanner.data.ItemSelectEvent
import ds.meterscanner.databinding.ActivityHistoryBinding
import ds.meterscanner.mvvm.BaseViewModel
import ds.meterscanner.mvvm.ListsView
import ds.meterscanner.mvvm.viewmodel.HistoryViewModel
import ds.meterscanner.util.post
import org.greenrobot.eventbus.Subscribe


class HistoryActivity : BaseActivity<ActivityHistoryBinding, HistoryViewModel>(), ListsView, ActionMode.Callback {

    private var actionMode: ActionMode? = null
    private var selectedItems = 0

    override fun provideViewModel(): HistoryViewModel = BaseViewModel(this)
    override fun getLayoutId(): Int = R.layout.activity_history

    override fun initViewModel() {
        super.initViewModel()
        viewModel.scrollToPositionCommand.observe(this) {
            post {
                binding.recyclerView.scrollToPosition(it)
            }
        }
    }


    override fun runDetails(snapshotId: String?) {
        runActivity<DetailsActivity>(DetailsActivity.REQUEST_DETAILS) {
            DetailsActivity::snapshotId..snapshotId
        }
    }

    @Subscribe
    fun onHistoryClickEvent(e: HistoryClickEvent) {
        runDetails(e.snapshot.id)
    }

    @Subscribe
    fun onItemSelectEvent(e: ItemSelectEvent) {
        selectedItems = e.totalSelected
        if (actionMode == null) {
            actionMode = startSupportActionMode(this)
        } else if (e.totalSelected == 0)
            actionMode?.finish()
        else
            actionMode?.invalidate()

    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        L.v("actionmode: onActionItemClicked")
        when (item.itemId) {
            R.id.item_delete -> {
                val count = selectedItems
                viewModel.deleteSelectedItems()
                mode.finish()
                showSnackbar(getString(R.string.removed_x_items, count.toString()))
            }
        }
        return true
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.history_actions, menu)
        viewModel.toggleSelectionMode(true)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.title = getString(R.string._items_selected, selectedItems.toString())
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        this.actionMode = null
        viewModel.toggleSelectionMode(false)
    }
}
