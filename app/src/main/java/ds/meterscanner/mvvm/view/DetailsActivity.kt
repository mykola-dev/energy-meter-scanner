package ds.meterscanner.mvvm.view

import android.databinding.ViewDataBinding
import ds.bindingtools.arg
import ds.meterscanner.R
import ds.meterscanner.mvvm.DetailsView
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.DetailsViewModel
import ds.meterscanner.ui.DatePickers

class DetailsActivity : BaseActivity<ViewDataBinding, DetailsViewModel>(), DetailsView {

    companion object {
        val REQUEST_DETAILS = 1
    }

    val snapshotId by arg<String>()

    override fun provideViewModel(): DetailsViewModel = viewModelOf(DetailsViewModel.Factory(snapshotId))

    override fun getLayoutId(): Int = R.layout.activity_details

    override fun pickDate() = DatePickers.pickDateTime(this, viewModel.truncDate(), viewModel::onDatePicked)

}
