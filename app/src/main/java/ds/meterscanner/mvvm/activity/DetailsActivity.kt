package ds.meterscanner.mvvm.activity

import android.databinding.ViewDataBinding
import android.os.Bundle
import ds.bindingtools.arg
import ds.meterscanner.R
import ds.meterscanner.mvvm.DetailsView
import ds.meterscanner.mvvm.viewmodel.DetailsViewModel
import ds.meterscanner.ui.DatePickers
import java.util.*

class DetailsActivity : BaseActivity<ViewDataBinding, DetailsViewModel>(), DetailsView {

    companion object {
        val REQUEST_DETAILS = 1
    }

    val snapshotId by arg<String>()

    override fun pickDate(initialDate: Date, callback: (Date) -> Unit) {
        DatePickers.pickDateTime(this, initialDate, callback)
    }

    override fun instantiateViewModel(state: Bundle?) = DetailsViewModel(this, snapshotId)
    override fun getLayoutId(): Int = R.layout.activity_details


}
