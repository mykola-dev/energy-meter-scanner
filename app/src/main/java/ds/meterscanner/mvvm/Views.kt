package ds.meterscanner.mvvm

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import java.util.*

interface BaseView : KodeinGlobalAware {
    val viewModel: BaseViewModel
    fun finish()
}

interface BindableView : KodeinGlobalAware {
    val viewModel: BindableViewModel
    fun finish()
}

interface SettingsView : BaseView
interface ChartsView : BindableView
interface AuthView : BindableView
interface ScannerView : BaseView

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

