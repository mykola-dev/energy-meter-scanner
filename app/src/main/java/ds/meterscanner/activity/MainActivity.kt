package ds.meterscanner.activity

import L
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import ds.bindingtools.arg
import ds.bindingtools.runActivity
import ds.bindingtools.runActivityForResult
import ds.meterscanner.R
import ds.meterscanner.databinding.MainBinding
import ds.meterscanner.databinding.MainView
import ds.meterscanner.databinding.viewmodel.MainViewModel
import ds.meterscanner.ui.DebugDrawerController

class MainActivity : BaseActivity<MainBinding, MainViewModel>(), MainView {

    val jobId by arg(-1)

    private var isIntentConsumed = false

    override fun instantiateViewModel(state: Bundle?) = MainViewModel(this, jobId ?: -1)
    override fun getLayoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DebugDrawerController(this).init()
        L.v("current job id=$jobId")
    }

    override fun runCamera(tries: Int, jobId: Int) {
        runActivityForResult<ScanAnalogMeterActivity>(requestCode = Requests.SCAN) {
            ScanAnalogMeterActivity::tries..tries
            ScanAnalogMeterActivity::jobId..jobId
            ScanAnalogMeterActivity::apiKey..viewModel.apiKey
        }
    }

    override fun runCharts() {
        runActivity<ChartsActivity>()
    }

    override fun runSettings() {
        runActivity<SettingsActivity>()
    }

    override fun runHistory() {
        runActivity<HistoryActivity>()
    }

    override fun runAlarms() {
        runActivity<AlarmsActivity>()
    }

    override fun requestSetupJobs(cb: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(R.string.attention)
            .setMessage(R.string.empty_jobs_message)
            .setPositiveButton(R.string.yes, { _, _ -> cb() })
            .setNegativeButton(R.string.no, null)
            .show()
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
        if (resultCode == Activity.RESULT_OK && data!=null) {
            val value = data.getDoubleExtra("value", -1.0)
            val bitmap = data.getParcelableExtra<Bitmap>("bitmap")
            viewModel.onNewData(value, bitmap, data.getBooleanExtra("corrected",false))
        } else {
            showSnackbar(getString(R.string.scan_error))
        }
    }

    override fun onLoggedIn() {
        handleIntent()
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

