package ds.meterscanner.databinding.viewmodel

import L
import android.databinding.ObservableField
import android.graphics.Bitmap
import ds.meterscanner.databinding.BaseViewModel
import ds.meterscanner.databinding.ScannerView
import ds.meterscanner.rx.applySchedulers
import ds.meterscanner.ui.widget.DimensionsCallback
import ds.meterscanner.util.ThreadTools

class ScannerViewModel(v: ScannerView, val tries: Int, val jobId: Int) : BaseViewModel<ScannerView>(v) {

    val results = arrayListOf<Double>()
    val bitmaps = arrayListOf<Bitmap>()

    val positionCallback = ObservableField<DimensionsCallback>()
    val scaleCallback = ObservableField<DimensionsCallback>()

    override fun onCreate() {
        super.onCreate()
        positionCallback.set({ x, y ->
            prefs.viewportX = x
            prefs.viewportY = y
            view.updateViewport()
        })
        scaleCallback.set { width, height ->
            prefs.viewportWidth = width
            prefs.viewportHeight = height
            view.updateViewport()
        }

        if (jobId < 0 && prefs.saveTemperature) {
            restService.getWeather()
                .map { it.main.temp }
                .applySchedulers()
                .bindTo(ViewModelEvent.DETACH)
                .subscribe({
                    prefs.currentTemperature = it.toFloat()
                    view.showSnackbar("Weather has been updated")
                }, Throwable::printStackTrace)
        }
    }

    private fun saveAndClose() {
        ThreadTools.release(jobId)
        if (validateResults()) {
            var resultString = results.average().toString()
            if (prefs.fixFirstFive && resultString.startsWith("6")) {
                resultString = resultString.replaceRange(0..0, "5")
                view.finishWithResult(resultString.toDouble(), bitmaps[0], corrected = true)
            } else {
                view.finishWithResult(results.average(), bitmaps[0])
            }
        } else {
            L.e("bad scan results!")
            view.finish()
        }
    }

    private fun validateResults(): Boolean {
        val average = results.average()
        return results.all { Math.abs(it - average) < 1 }
    }

    fun onScanResult(result: String, bitmap: Bitmap) {
        L.v("scan result=$result")
        results += result.toDoubleOrNull() ?: return
        bitmaps += bitmap
        if (results.size < tries) {
            view.startScanning()
        } else
            saveAndClose()
    }

}