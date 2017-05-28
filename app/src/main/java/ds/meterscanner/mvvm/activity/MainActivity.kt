package ds.meterscanner.mvvm.activity

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
import ds.meterscanner.mvvm.viewmodel.MainViewModel
import ds.meterscanner.ui.DebugDrawerController
import ds.meterscanner.util.provideViewModel

class MainActivity : BaseActivity2<MainBinding, MainViewModel>() {

    val jobId by arg(-1)

    private var isIntentConsumed = false

    override fun provideViewModel(state: Bundle?): MainViewModel = provideViewModel()

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
        viewModel.runHistoryCommand.observe(this) {
            runActivity<HistoryActivity>()
        }
        viewModel.runSettingsCommand.observe(this) {
            runActivity<SettingsActivity>()
        }
        viewModel.runChartsCommand.observe(this) {
            runActivity<ChartsActivity>()
        }
        viewModel.runCameraCommand.observe(this) {
            runActivityForResult<ScanAnalogMeterActivity>(requestCode = Requests.SCAN) {
                ScanAnalogMeterActivity::tries..it.tries
                ScanAnalogMeterActivity::jobId..it.jobId
                ScanAnalogMeterActivity::apiKey..viewModel.apiKey
            }
        }
        viewModel.onLoggedInCommand.observe(this) {
            handleIntent()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(this::isIntentConsumed.name, isIntentConsumed)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isIntentConsumed = savedInstanceState.getBoolean(this::isIntentConsumed.name)
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
        isIntentConsumed = false
        handleIntent()
    }

    fun handleIntent() {
        if (isIntentConsumed) {
            L.w("intent has been already consumed")
            return
        }
        isIntentConsumed = true

        if (jobId!! >= 0) {
            L.v("going to make a snapshot!")
            viewModel.takeSnapshot(jobId ?: -1)
        } else {
            L.w("empty intent")
        }
    }

}

