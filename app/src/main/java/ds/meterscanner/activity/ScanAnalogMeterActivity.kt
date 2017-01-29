/*
 * Anyline
 * ScanEnergyActivity.java
 *
 * Copyright (c) 2015 9yards GmbH
 *
 * Created by martin at 2015-07-03
 */

package ds.meterscanner.activity

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
import ds.meterscanner.data.InterruptEvent
import ds.meterscanner.databinding.ActivityScanEnergyBinding
import ds.meterscanner.databinding.ScannerView
import ds.meterscanner.databinding.viewmodel.ScannerViewModel
import org.greenrobot.eventbus.Subscribe


class ScanAnalogMeterActivity : BaseActivity<ActivityScanEnergyBinding, ScannerViewModel>(), CameraOpenListener, ScannerView {

    val tries by arg<Int>()
    val jobId by arg<Int>()
    val apiKey by arg<String>()

    override fun instantiateViewModel(state: Bundle?): ScannerViewModel = ScannerViewModel(this, tries!!, jobId!!)

    override fun getLayoutId(): Int = R.layout.activity_scan_energy

    private lateinit var energyScanView: EnergyScanView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        energyScanView = binding.energyScanView

        initView()
    }

    fun initView() {
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

        // set reporting according to prefs or true on default
        energyScanView.setReportingEnabled(false)

        // initialize Anyline with the license key and a Listener that is called if a result is found
        energyScanView.initAnyline(apiKey) { scanMode, result, resultImage, fullImage ->
            viewModel.onScanResult(result, resultImage.bitmap)
        }


        updateViewport()

    }

    override fun updateViewport() {
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


    override fun startScanning() {
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
        // This is useful to present an alternative way to enter the required data if no camera exists.
        throw RuntimeException(e)
    }

    @Subscribe
    fun onInterruptEvent(e: InterruptEvent) {
        // finish immediately
        finish()
    }

    override fun finishWithResult(value: Double, bitmap: Bitmap?, corrected: Boolean) {
        val data = Intent().putExtras(bundle {
            "value"..value
            "bitmap"..bitmap
            "corrected"..corrected
        })
        setResult(Activity.RESULT_OK, data)
        finish()
    }

}
