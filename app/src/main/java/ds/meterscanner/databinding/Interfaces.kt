package ds.meterscanner.databinding

import android.graphics.Bitmap
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import com.github.salomonbrys.kodein.KodeinAware
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

interface MainView : BaseView {
    fun runCamera(tries: Int, jobId: Int)
    fun runCharts()
    fun runSettings()
    fun runHistory()
    fun requestSetupJobs(cb: () -> Unit)
    fun onLoggedIn()
    fun runAlarms()
}

interface SettingsView : BaseView

interface ChartsView : BaseView

interface ListsView : BaseView {
    fun runDetails(snapshotId: String?)
    fun scrollToPosition(position: Int)
}

interface AlarmsView : BaseView {
    fun pickTime(time: Date, callback: (hour: Int, minute: Int) -> Unit)
}

interface AuthView : BaseView
interface ScannerView : BaseView {
    fun finishWithResult(value: Double, bitmap: Bitmap? = null, corrected: Boolean = false)
    fun startScanning()
    fun updateViewport()
}

interface DetailsView : BaseView {
    fun pickDate(initialDate: Date, callback: (Date) -> Unit)
}

