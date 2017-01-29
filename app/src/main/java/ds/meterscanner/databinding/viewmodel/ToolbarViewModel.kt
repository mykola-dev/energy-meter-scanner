package ds.meterscanner.databinding.viewmodel

import android.databinding.BaseObservable
import android.databinding.ObservableField

class ToolbarViewModel : BaseObservable() {
    val title = ObservableField<String>()
    val subtitle = ObservableField<String>()
}