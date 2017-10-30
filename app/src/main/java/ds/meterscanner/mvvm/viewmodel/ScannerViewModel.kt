package ds.meterscanner.mvvm.viewmodel

import L
import android.graphics.Bitmap
import com.github.salomonbrys.kodein.Kodein
import ds.meterscanner.coroutines.Locker
import ds.meterscanner.mvvm.BindableViewModel
import ds.meterscanner.mvvm.Command
import ds.meterscanner.mvvm.FinishWithResultCommand
import ds.meterscanner.mvvm.invoke

class ScannerViewModel(
    kodein: Kodein,
    private val tries: Int,
    private val jobId: Int
) : BindableViewModel(kodein) {

    val startScanningCommand = Command<Unit>()
    val finishWithResultCommand = FinishWithResultCommand()

    private val results = arrayListOf<Double>()
    private val bitmaps = arrayListOf<Bitmap>()

    init {
        if (jobId == 0 && prefs.saveTemperature) {
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
        Locker.release(jobId)
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

    fun saveViewportSize(width: Int, height: Int) {
        prefs.viewportWidth = width
        prefs.viewportHeight = height
    }

    fun saveViewportPosition(x: Int, y: Int) {
        prefs.viewportX = x
        prefs.viewportY = y
    }

}