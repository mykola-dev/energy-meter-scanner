package ds.meterscanner.mvvm

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import java.util.*

interface BaseView : KodeinGlobalAware {
    val viewModel: BaseViewModel
    fun finish()
}

interface SettingsView : BaseView

interface ChartsView : BaseView

interface AuthView : BaseView

interface ScannerView : BaseView

interface MainView : BaseView {
    fun onCameraButton()
    fun onListsButton()
    fun onChartsButton()
    fun onSettingsButton()
}

interface ListsView : BaseView {
    fun runDetails(snapshotId: String?)
}

interface AlarmsView : BaseView {
    fun pickTime(time: Date, callback: (hour: Int, minute: Int) -> Unit)
}


interface DetailsView : BaseView {
    fun onDatePick()
    fun onSave()
}

