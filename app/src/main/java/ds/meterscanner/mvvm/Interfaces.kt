package ds.meterscanner.mvvm

import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.LazyKodeinAware
import java.util.*

interface ViewModel {
    val view: View

    fun onCreate()
    fun onAttach()
    fun onDetach()
    fun onDestroy()
}

interface View {
    val viewModel: ViewModel
}

interface View3 : LazyKodeinAware {
    val viewModel: BaseViewModel3
    fun finish()
}

@Deprecated("")
interface BaseView : View, KodeinAware {
    fun runAuthScreen()
    fun getString(@StringRes id: Int): String
    fun getColour(@ColorRes id: Int): Int
    fun showSnackbar(
        text: String,
        callback: (() -> Unit)? = null,
        duration: Int = Snackbar.LENGTH_LONG,
        @StringRes actionText: Int = 0,
        actionCallback: (() -> Unit)? = null
    )

    fun finish()
}

interface SettingsView : BaseView
interface ChartsView : BaseView
interface AuthView : View3
interface MainView : View3 {
    fun onCameraButton()
    fun onListsButton()
    fun onChartsButton()
    fun onSettingsButton()
}

interface ListsView : BaseView {
    fun runDetails(snapshotId: String?)
    fun scrollToPosition(position: Int)
}

interface AlarmsView : View3 {
    fun pickTime(time: Date, callback: (hour: Int, minute: Int) -> Unit)
    /*fun onNewAlarm()
    fun onEditAlarm(jobId:Int)
    fun onDeleteAlarm(jobId:Int)*/
}

interface ScannerView : View3 {
    //fun finishWithResult(value: Double, bitmap: Bitmap? = null, corrected: Boolean = false)
    //fun startScanning()
    //fun updateViewport()
}

interface DetailsView : View3 {
    fun onDatePick()
    fun onSave()
}

