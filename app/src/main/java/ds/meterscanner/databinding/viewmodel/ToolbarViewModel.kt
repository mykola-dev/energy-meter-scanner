package ds.meterscanner.databinding.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import ds.bindingtools.observableField

class ToolbarViewModel : BaseObservable() {
    @get:Bindable var title by observableField<String>()
    @get:Bindable var subtitle by observableField<String>()
}