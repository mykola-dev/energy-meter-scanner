package ds.meterscanner.mvvm.viewmodel

import ds.meterscanner.R
import ds.meterscanner.mvvm.BaseViewModel

class SettingsViewModel : BaseViewModel() {

    init {
        toolbar.title = getString(R.string.settings)
    }
}