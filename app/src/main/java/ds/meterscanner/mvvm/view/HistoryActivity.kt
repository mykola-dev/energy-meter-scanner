package ds.meterscanner.mvvm.view

import L
import android.support.v7.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import ds.bindingtools.startActivity
import ds.meterscanner.R
import ds.meterscanner.adapter.HistoryAdapter
import ds.meterscanner.databinding.ActivityHistoryBinding
import ds.meterscanner.mvvm.ListsView
import ds.meterscanner.mvvm.observe
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.HistoryViewModel
import ds.meterscanner.util.post


class HistoryActivity : BaseActivity<ActivityHistoryBinding, HistoryViewModel>(), ListsView, ActionMode.Callback {

    override val adapter: HistoryAdapter
        get() = HistoryAdapter(
            viewModel.isActionMode.value ?: false,
            { snapshot -> navigateDetails(snapshot.id) },
            ::onToggleSelection
        )

    private var actionMode: ActionMode? = null
    private var selectedItems = 0

    override fun provideViewModel(): HistoryViewModel = viewModelOf()
    override fun getLayoutId(): Int = R.layout.activity_history

    override fun initViewModel() {
        super.initViewModel()
        viewModel.scrollToPositionCommand.observe(this) {
            post { binding.recyclerView.scrollToPosition(it) }
        }

        viewModel.isActionMode.observe(this) {
            (binding.recyclerView.adapter as? HistoryAdapter)
                ?.isActionMode = it
        }

        onToggleSelection()
    }

    private fun onToggleSelection() {
        selectedItems = viewModel.getSeledtedItemsCount()
        when {
            selectedItems == 0 -> actionMode?.finish()
            actionMode != null -> actionMode!!.invalidate()
            else -> post { actionMode = startSupportActionMode(this) }  // hack
        }
    }

    override fun navigateDetails(snapshotId: String?) {
        startActivity<DetailsActivity>(DetailsActivity.REQUEST_DETAILS) {
            DetailsActivity::snapshotId to snapshotId
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        viewModel.isActionMode.value = true
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.title = getString(R.string._items_selected, selectedItems.toString())
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        this.actionMode = null
        viewModel.isActionMode.value = false
    }
}
