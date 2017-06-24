package ds.meterscanner.mvvm.view

import android.databinding.ViewDataBinding
import ds.bindingtools.arg
import ds.meterscanner.R
import ds.meterscanner.mvvm.DetailsView
import ds.meterscanner.mvvm.viewmodel.DetailsViewModel
import ds.meterscanner.ui.DatePickers

class DetailsActivity : BaseActivity3<ViewDataBinding, DetailsViewModel>(), DetailsView {

    companion object {
        val REQUEST_DETAILS = 1
    }

    val snapshotId by arg<String>()

    override fun provideViewModel() = DetailsViewModel(this, snapshotId)

    override fun getLayoutId(): Int = R.layout.activity_details

    override fun onDatePick() = DatePickers.pickDateTime(this, viewModel.truncDate(), viewModel::onDatePicked)

    override fun onSave() = viewModel.doSave(this)

}
