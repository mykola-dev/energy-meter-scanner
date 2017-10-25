package ds.meterscanner.mvvm.view

import L
import android.support.v7.view.ActionMode
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutCompat
import android.view.Menu
import android.view.MenuItem
import ds.bindingtools.startActivity
import ds.bindingtools.withBindable
import ds.meterscanner.R
import ds.meterscanner.adapter.HistoryAdapter
import ds.meterscanner.mvvm.BindableActivity
import ds.meterscanner.mvvm.ListsView
import ds.meterscanner.mvvm.observe
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.HistoryViewModel
import ds.meterscanner.util.post
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.toolbar.*

class HistoryActivity : BindableActivity<HistoryViewModel>(), ListsView, ActionMode.Callback {

    private var actionMode: ActionMode? = null
    private var selectedItems = 0

    override fun provideViewModel(): HistoryViewModel = viewModelOf()
    override fun getLayoutId(): Int = R.layout.activity_history

    override fun bindView() {
        super.bindView()
        toolbar.title = getString(R.string.history)

        val adapter = HistoryAdapter(
            onItemClick = { snapshot -> navigateDetails(snapshot.id) },
            onToggleSelection = ::onToggleSelection
        )

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutCompat.VERTICAL))
        fab.setOnClickListener { viewModel.onNewSnapshot(this) }

        withBindable(viewModel) {
            bind(::isActionMode, adapter::isActionMode)
            bind(::listItems, adapter::data)
            bind(::subTitle,toolbar::setSubtitle)
        }
    }

    override fun initViewModel() {
        super.initViewModel()
        viewModel.scrollToPositionCommand.observe(this) {
            post { recyclerView.scrollToPosition(it) }
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
        viewModel.isActionMode = true
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.title = getString(R.string._items_selected, selectedItems.toString())
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        this.actionMode = null
        viewModel.isActionMode = false
    }
}
