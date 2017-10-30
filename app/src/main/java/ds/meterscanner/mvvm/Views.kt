package ds.meterscanner.mvvm

import java.util.*

interface BindableView {
    val viewModel: BindableViewModel
    fun finish()
}

interface SettingsView : BindableView
interface ChartsView : BindableView
interface AuthView : BindableView
interface ScannerView : BindableView

interface MainView : BindableView {
    fun navigateCameraScreen()
    fun navigateListsScreen()
    fun navigateChartsScreen()
    fun navigateSettingsScreen()
}

interface ListsView : BindableView {
    fun navigateDetails(snapshotId: String?)
}

interface AlarmsView : BindableView {
    fun pickTime(time: Date, callback: (hour: Int, minute: Int) -> Unit)
}

interface DetailsView : BindableView {
    fun pickDate()
}
