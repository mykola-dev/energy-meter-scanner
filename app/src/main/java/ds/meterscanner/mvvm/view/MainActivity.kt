package ds.meterscanner.mvvm.view

import L
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import ds.bindingtools.arg
import ds.bindingtools.startActivity
import ds.bindingtools.startActivityForResult
import ds.meterscanner.R
import ds.meterscanner.databinding.MainBinding
import ds.meterscanner.mvvm.MainView
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.MainViewModel
import ds.meterscanner.ui.DebugDrawerController

class MainActivity : BaseActivity<MainBinding, MainViewModel>(), MainView {

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
            startActivity<AlarmsActivity>()
        }
        viewModel.onLoggedInCommand.observe(this) {
            handleIntent()
        }
    }

    override fun onCameraButton() = startActivityForResult<ScanAnalogMeterActivity>(requestCode = Requests.SCAN) {
        ScanAnalogMeterActivity::tries to 1
        ScanAnalogMeterActivity::jobId to -1
        ScanAnalogMeterActivity::apiKey to viewModel.apiKey
    }

    override fun onListsButton() = startActivity<HistoryActivity>()
    override fun onChartsButton() = startActivity<ChartsActivity>()
    override fun onSettingsButton() = startActivity<SettingsActivity>()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

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
            startActivityForResult<ScanAnalogMeterActivity>(requestCode = Requests.SCAN) {
                ScanAnalogMeterActivity::tries to viewModel.prefs.scanTries
                ScanAnalogMeterActivity::jobId to jobId
                ScanAnalogMeterActivity::apiKey to viewModel.apiKey
            }
        } else {
            L.w("empty intent")
        }
    }

}

