package ds.meterscanner.mvvm.viewmodel

import L
import android.databinding.ObservableField
import android.graphics.Bitmap
import ds.meterscanner.mvvm.BaseViewModel3
import ds.meterscanner.mvvm.Command
import ds.meterscanner.mvvm.FinishWithResultCommand
import ds.meterscanner.mvvm.invoke
import ds.meterscanner.ui.widget.DimensionsCallback
import ds.meterscanner.util.ThreadTools

class ScannerViewModel : BaseViewModel3() {

    val updateViewPortCommand = Command<Unit>()
    val startScanningCommand = Command<Unit>()
    val finishWithResultCommand = FinishWithResultCommand()

    var tries: Int = 0
    var jobId: Int = 0

    val results = arrayListOf<Double>()
    val bitmaps = arrayListOf<Bitmap>()

    val positionCallback = ObservableField<DimensionsCallback>()
    val scaleCallback = ObservableField<DimensionsCallback>()

    init {
        positionCallback.set({ x, y ->
            prefs.viewportX = x
            prefs.viewportY = y
            updateViewPortCommand()
        })
        scaleCallback.set { width, height ->
            prefs.viewportWidth = width
            prefs.viewportHeight = height
            updateViewPortCommand()
        }

        if (jobId < 0 && prefs.saveTemperature) {
            updateWeather()
        }
    }

    private fun updateWeather() = async {
        try {
            val weather = restService.getWeather().main.temp.toFloat()
            prefs.currentTemperature = weather
            showSnackbarCommand("Weather has been updated")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveAndClose() {
        ThreadTools.release(jobId)
        if (validateResults()) {
            var resultString = results.average().toString()
            if (prefs.fixFirstFive && resultString.startsWith("6")) {
                resultString = resultString.replaceRange(0..0, "5")
                finishWithResultCommand(resultString.toDouble(), bitmaps[0], corrected = true)
            } else {
                finishWithResultCommand(results.average(), bitmaps[0])
            }
        } else {
            L.e("bad scan results!")
            finishCommand()
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
            startScanningCommand()
        } else
            saveAndClose()
    }

}