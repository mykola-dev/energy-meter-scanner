package ds.meterscanner.mvvm.view

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.salomonbrys.kodein.kodein
import ds.bindingtools.arg
import ds.bindingtools.withBindable
import ds.meterscanner.R
import ds.meterscanner.mvvm.BindableActivity
import ds.meterscanner.mvvm.DetailsView
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.DetailsViewModel
import ds.meterscanner.ui.DatePickers
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.toolbar.*

class DetailsActivity : BindableActivity<DetailsViewModel>(), DetailsView {

    companion object {
        val REQUEST_DETAILS = 1
    }

    val snapshotId by arg<String>()

    override fun provideViewModel(): DetailsViewModel = viewModelOf { DetailsViewModel(kodein().value, snapshotId) }

    override fun getLayoutId(): Int = R.layout.activity_details

    override fun pickDate() = DatePickers.pickDateTime(this, viewModel.truncDate(), viewModel::onDatePicked)

    override fun bindView() {
        super.bindView()
        dateField.keyListener = null
        datePickButton.setOnClickListener { viewModel.onDatePick(this) }
        saveButton.setOnClickListener { viewModel.onSave(this) }

        withBindable(viewModel) {
            bind(::imageUrl, {
                glide
                    .load(it)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView)
            })
            bind(::value, valueField)
            bind(valueError::error, valueLayout::setError)
            bind(::date, dateField)
            bind(::outsideTemp, outsideTempField)
            bind(::boilerTemp, boilerTempField)
            bind(::toolbarTitle, toolbar::setTitle, toolbar::getTitle)
        }
    }
}
