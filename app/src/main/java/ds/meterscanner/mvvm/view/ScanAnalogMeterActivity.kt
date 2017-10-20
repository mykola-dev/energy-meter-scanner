/*
 * Anyline
 * ScanEnergyActivity.java
 *
 * Copyright (c) 2015 9yards GmbH
 *
 * Created by martin at 2015-07-03
 */

package ds.meterscanner.mvvm.view

import L
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.WindowManager
import at.nineyards.anyline.camera.CameraController
import at.nineyards.anyline.camera.CameraFeatures
import at.nineyards.anyline.camera.CameraOpenListener
import at.nineyards.anyline.modules.energy.EnergyScanView
import ds.bindingtools.arg
import ds.bindingtools.bundle
import ds.meterscanner.R
import ds.meterscanner.data.EventCallback
import ds.meterscanner.data.INTERRUPT_EVENT
import ds.meterscanner.data.subscribeEvent
import ds.meterscanner.data.unsubscribeEvent
import ds.meterscanner.mvvm.BindableActivity
import ds.meterscanner.mvvm.ScannerView
import ds.meterscanner.mvvm.observe
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.ScannerViewModel
import kotlinx.android.synthetic.main.activity_scan_energy.*


class ScanAnalogMeterActivity : BindableActivity<ScannerViewModel>(), CameraOpenListener, ScannerView {

    val tries by arg<Int>()
    val jobId by arg<Int>()
    val apiKey by arg<String>()

    private val interruptReceiver = EventCallback {
        finish()
    }

    override fun provideViewModel() = viewModelOf<ScannerViewModel>().also { it.tries = tries!!; it.jobId = jobId!! }

    override fun getLayoutId(): Int = R.layout.activity_scan_energy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        initView()
    }

    override fun onStart() {
        super.onStart()
        subscribeEvent(INTERRUPT_EVENT, interruptReceiver)
    }

    override fun onStop() {
        super.onStop()
        unsubscribeEvent(interruptReceiver)
    }

    override fun initViewModel() {
        super.initViewModel()
        viewModel.startScanningCommand.observe(this) {
            startScanning()
        }
        viewModel.finishWithResultCommand.observe(this) {
            finishWithResult(it.value, it.bitmap, it.corrected)
        }
    }

    private fun initView() {
        energyScanView.positionCallback = { x, y ->
            viewModel.saveViewportPosition(x,y)
            updateViewport()
        }
        energyScanView.scaleCallback = { width, height ->
            viewModel.saveViewportSize(width,height)
            updateViewport()
        }

        energyScanView.scanMode = EnergyScanView.ScanMode.ANALOG_METER

        // set individual camera settings for this example by getting the current preferred settings and adapting them
        with(energyScanView.preferredCameraConfig) {
            focusMode = CameraFeatures.FocusMode.MACRO
            // autofocus is called in this interval (8000 is default)
            autoFocusInterval = 8000
            isAutoExposureRegionEnabled = true
            //sceneMode = CameraFeatures.SceneMode.SNOW
            //previewSize = CameraSize(640,480)
            //pictureSize = CameraSize(640,480)
            isUpdateRegionsOnAutoFocusEnabled = false
        }

        // add a camera open listener that will be called when the camera is opened or an error occurred
        //  this is optional (if not set a RuntimeException will be thrown if an error occurs)
        energyScanView.setCameraOpenListener(this)

        // set reporting according bind prefs or true on default
        energyScanView.setReportingEnabled(false)

        // initialize Anyline with the license key and a Listener that is called if a result is found
        energyScanView.initAnyline(apiKey, { result ->
            viewModel.onScanResult(result.result, result.cutoutImage!!.bitmap)
        })

        updateViewport()

    }

    private fun updateViewport() {
        val prefs = viewModel.prefs
        with(energyScanView.config) {
            if (prefs.viewportX >= 0 && prefs.viewportY >= 0) {
                cutoutOffsetX = prefs.viewportX
                cutoutOffsetY = prefs.viewportY
            }

            if (prefs.viewportWidth > 0) {
                cutoutWidth = prefs.viewportWidth
            }

            if (prefs.viewportHeight > 0) {
                cutoutRatio = cutoutWidth.toFloat() / prefs.viewportHeight
            }
        }

        energyScanView.updateCutoutView()
    }


    private fun startScanning() {
        energyScanView.startScanning()
    }

    override fun onResume() {
        super.onResume()
        startScanning()
    }


    override fun onPause() {
        energyScanView.cancelScanning()
        //release the camera (must be called in onPause, because there are situations where it cannot be auto-detected that the camera should be released)
        energyScanView.releaseCameraInBackground()
        super.onPause()
    }

    override fun onCameraOpened(cameraController: CameraController, width: Int, height: Int) {
        //the camera is opened async and this is called when the opening is finished
        L.d("Camera opened successfully. Frame resolution $width x $height")
    }

    override fun onCameraError(e: Exception) {
        //This is called if the camera could not be opened.
        // (e.g. If there is no camera or the permission is denied)
        // This is useful bind present an alternative way bind enter the required data if no camera exists.
        throw RuntimeException(e)
    }

    private fun finishWithResult(value: Double, bitmap: Bitmap?, corrected: Boolean) {
        val data = Intent().putExtras(bundle {
            "value" to value
            "bitmap" to bitmap
            "corrected" to corrected
        })
        setResult(Activity.RESULT_OK, data)
        finish()
    }

}
