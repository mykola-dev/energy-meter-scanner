package ds.meterscanner.mvvm

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware

interface BaseView : KodeinGlobalAware {
    val viewModel: BaseViewModel
    fun finish()
}

interface SettingsView : BaseView
interface ChartsView : BaseView
interface AuthView : BaseView
interface ScannerView : BaseView
interface AlarmsView : BaseView

interface MainView : BaseView {
    fun navigateCameraScreen()
    fun navigateListsScreen()
    fun navigateChartsScreen()
    fun navigateSettingsScreen()
}

interface ListsView : BaseView {
    fun runDetails(snapshotId: String?)
}


interface DetailsView : BaseView {
    fun pickDate()
}

