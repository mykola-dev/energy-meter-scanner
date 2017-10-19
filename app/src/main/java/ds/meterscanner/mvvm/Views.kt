package ds.meterscanner.mvvm

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import ds.meterscanner.adapter.AlarmsAdapter
import ds.meterscanner.adapter.HistoryAdapter
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
interface ChartsView : BaseView
interface AuthView : BindableView
interface ScannerView : BaseView

interface MainView : BaseView {
    fun navigateCameraScreen()
    fun navigateListsScreen()
    fun navigateChartsScreen()
    fun navigateSettingsScreen()
}

interface ListsView : BaseView {
    fun navigateDetails(snapshotId: String?)
    val adapter: HistoryAdapter
}

interface AlarmsView : BaseView {
    fun pickTime(time: Date, callback: (hour: Int, minute: Int) -> Unit)
    val adapter: AlarmsAdapter
}

interface DetailsView : BaseView {
    fun pickDate()
}

