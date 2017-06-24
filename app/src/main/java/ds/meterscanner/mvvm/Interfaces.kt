package ds.meterscanner.mvvm

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import java.util.*

interface View : KodeinGlobalAware {
    val viewModel: BaseViewModel
    fun finish()
}

interface SettingsView : View
interface ChartsView : View
interface AuthView : View
interface MainView : View {
    fun onCameraButton()
    fun onListsButton()
    fun onChartsButton()
    fun onSettingsButton()
}

interface ListsView : View {
    fun runDetails(snapshotId: String?)
}

interface AlarmsView : View {
    fun pickTime(time: Date, callback: (hour: Int, minute: Int) -> Unit)
}

interface ScannerView : View

interface DetailsView : View {
    fun onDatePick()
    fun onSave()
}

