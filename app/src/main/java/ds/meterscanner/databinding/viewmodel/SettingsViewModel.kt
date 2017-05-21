package ds.meterscanner.databinding.viewmodel

import ds.meterscanner.R
import ds.meterscanner.databinding.BaseViewModel
import ds.meterscanner.databinding.SettingsView

class SettingsViewModel(v: SettingsView) : BaseViewModel<SettingsView>(v) {

    override fun onCreate() {
        super.onCreate()
        toolbar.title = view.getString(R.string.settings)

    }
}