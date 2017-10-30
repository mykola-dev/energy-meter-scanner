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
import ds.bindingtools.withBindable
import ds.meterscanner.R
import ds.meterscanner.mvvm.BindableActivity
import ds.meterscanner.mvvm.MainView
import ds.meterscanner.mvvm.observe
import ds.meterscanner.mvvm.viewmodel.MainViewModel
import ds.meterscanner.ui.DebugDrawerController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BindableActivity<MainViewModel>(), MainView {

    val jobId by arg(-1)

    override fun provideViewModel(): MainViewModel = defaultViewModelOf()

    override fun getLayoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DebugDrawerController(this)
        L.v("current job id=$jobId")
    }

    override val isDisplayUpButton: Boolean get() = false

    override fun bindView() {
        super.bindView()
        gridLayout.columnCount = resources.getInteger(R.integer.columns)

        cameraButton.setOnClickListener { viewModel.onCameraButton(this) }
        chartsButton.setOnClickListener { viewModel.onChartsButton(this) }
        historyButton.setOnClickListener { viewModel.onListsButton(this) }
        settingsButton.setOnClickListener { viewModel.onSettingsButton(this) }

        withBindable(viewModel) {
            bind(::lastUpdated, lastUpdatedLabel)
            bind(::buttonsEnabled, {
                cameraButton.isEnabled = it && apiKey.isNotEmpty()
                chartsButton.isEnabled = it
                historyButton.isEnabled = it
                settingsButton.isEnabled = it
            })
            bind(::apiKey, { cameraButton.isEnabled = it.isNotEmpty() && buttonsEnabled })
            bind(::toolbarSubtitle, toolbar::setSubtitle, toolbar::getSubtitle)
        }
    }

    override fun initViewModel() {
        super.initViewModel()
        viewModel.jobId = jobId
        viewModel.runAlarmsCommand.observe(this) {
            startActivity<AlarmsActivity>()
        }
        viewModel.onLoggedInCommand.observe(this) {
            handleIntent()
        }
    }

    override fun navigateCameraScreen() = startActivityForResult<ScanAnalogMeterActivity>(requestCode = Requests.SCAN) {
        ScanAnalogMeterActivity::tries to 1
        ScanAnalogMeterActivity::jobId to -1
        ScanAnalogMeterActivity::apiKey to viewModel.apiKey
    }

    override fun navigateListsScreen() = startActivity<HistoryActivity>()
    override fun navigateChartsScreen() = startActivity<ChartsActivity>()
    override fun navigateSettingsScreen() = startActivity<SettingsActivity>()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_version).title = "v${viewModel.appVersion}"

        menu.findItem(R.id.action_logout).setOnMenuItemClickListener {
            viewModel.logout()
            true
        }
        return super.onPrepareOptionsMenu(menu)
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
        viewModel.jobId = jobId
        viewModel.isIntentConsumed = false
        handleIntent()
    }

    private fun handleIntent() {
        if (viewModel.isIntentConsumed) {
            L.w("intent has been already consumed")
            return
        }
        viewModel.isIntentConsumed = true

        if (jobId >= 0) {
            L.v("going bind make a snapshot!")
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

