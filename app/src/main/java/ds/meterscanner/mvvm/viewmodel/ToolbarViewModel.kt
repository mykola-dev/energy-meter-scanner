package ds.meterscanner.mvvm.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import ds.meterscanner.mvvm.observableField

class ToolbarViewModel : BaseObservable() {
    @get:Bindable var title by observableField<String>()
    @get:Bindable var subtitle by observableField<String>()
}