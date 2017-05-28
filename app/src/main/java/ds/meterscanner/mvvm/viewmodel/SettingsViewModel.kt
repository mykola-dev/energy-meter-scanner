package ds.meterscanner.mvvm.viewmodel

import ds.meterscanner.R
import ds.meterscanner.mvvm.BaseViewModel
import ds.meterscanner.mvvm.SettingsView

class SettingsViewModel(v: SettingsView) : BaseViewModel<SettingsView>(v) {

    override fun onCreate() {
        super.onCreate()
        toolbar.title = view.getString(R.string.settings)

    }
}