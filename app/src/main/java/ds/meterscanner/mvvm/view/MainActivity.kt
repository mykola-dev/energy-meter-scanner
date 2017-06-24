package ds.meterscanner.mvvm.view

import L
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import ds.bindingtools.arg
import ds.bindingtools.runActivity
import ds.bindingtools.runActivityForResult
import ds.meterscanner.R
import ds.meterscanner.databinding.MainBinding
import ds.meterscanner.mvvm.MainView
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.MainViewModel
import ds.meterscanner.ui.DebugDrawerController

class MainActivity : BaseActivity3<MainBinding, MainViewModel>(), MainView {

    val jobId by arg(-1)

    override fun provideViewModel(): MainViewModel = viewModelOf()

    override fun getLayoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DebugDrawerController(this)
        L.v("current job id=$jobId")
    }

    override fun initViewModel() {
        super.initViewModel()
        viewModel.jobId = jobId ?: -1
        viewModel.runAlarmsCommand.observe(this) {
            runActivity<AlarmsActivity>()
        }
        viewModel.onLoggedInCommand.observe(this) {
            handleIntent()
        }
    }

    override fun onCameraButton() = runActivityForResult<ScanAnalogMeterActivity>(requestCode = Requests.SCAN) {
        ScanAnalogMeterActivity::tries..1
        ScanAnalogMeterActivity::jobId..-1
        ScanAnalogMeterActivity::apiKey..viewModel.apiKey
    }

    override fun onListsButton() = runActivity<HistoryActivity>()
    override fun onChartsButton() = runActivity<ChartsActivity>()
    override fun onSettingsButton() = runActivity<SettingsActivity>()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

   /* override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(this::isIntentConsumed.name, isIntentConsumed)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isIntentConsumed = savedInstanceState.getBoolean(this::isIntentConsumed.name)
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val value = data.getDoubleExtra("value", -1.0)
            val bitmap = data.getParcelableExtra<Bitmap>("bitmap")
            viewModel.onNewData(value, bitmap, data.getBooleanExtra("corrected", false))
        } else {
            showSnackbar(getString(R.string.scan_error))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        viewModel.jobId = jobId ?: -1
        viewModel.isIntentConsumed = false
        handleIntent()
    }

    fun handleIntent() {
        if (viewModel.isIntentConsumed) {
            L.w("intent has been already consumed")
            return
        }
        viewModel.isIntentConsumed = true

        if (jobId!! >= 0) {
            L.v("going to make a snapshot!")
            runActivityForResult<ScanAnalogMeterActivity>(requestCode = Requests.SCAN) {
                ScanAnalogMeterActivity::tries..viewModel.prefs.scanTries
                ScanAnalogMeterActivity::jobId..jobId
                ScanAnalogMeterActivity::apiKey..viewModel.apiKey
            }
        } else {
            L.w("empty intent")
        }
    }

}

