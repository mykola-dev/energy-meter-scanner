package ds.meterscanner.databinding.viewmodel

import android.databinding.BaseObservable
import android.databinding.ObservableField
import ds.bindingtools.ViewModel
import ds.bindingtools.binding

class ToolbarViewModel : BaseObservable(), ViewModel {
    @Deprecated("use anko bindings")
    val title = ObservableField<String>()
    @Deprecated("use anko bindings")
    val subtitle = ObservableField<String>()

    var title_: CharSequence by binding()
    var subtitle_: CharSequence by binding()
}