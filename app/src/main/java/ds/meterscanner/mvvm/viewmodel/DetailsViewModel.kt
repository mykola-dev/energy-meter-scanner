package ds.meterscanner.mvvm.viewmodel

import L
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import com.github.salomonbrys.kodein.erased.instance
import ds.meterscanner.R
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.mvvm.BaseViewModel
import ds.meterscanner.mvvm.DetailsView
import ds.meterscanner.util.formatTimeDate
import java.util.*

class DetailsViewModel(
    private val snapshotId: String? = null
) : BaseViewModel() {

    val valueField = ObservableField<String>()
    val dateField = ObservableField<String>()
    val valueErrorField = ObservableField<String>()
    val imageUrl = ObservableField<String>()
    val outsideTemp = ObservableField<String>()
    val boilerTemp = ObservableField<String>()

    lateinit var snapshot: Snapshot
    private val calendar: Calendar = instance()

    init {
        if (snapshotId != null) {
            toolbar.title = getString(R.string.edit_snapshot)
        } else {
            toolbar.title = getString(R.string.new_snapshot)
        }

        fetchSnapshot()
    }

    private fun fetchSnapshot() = async {
        snapshot = if (snapshotId != null)
            db.getSnapshotById(snapshotId)
        else
            Snapshot(boilerTemp = prefs.boilerTemp, outsideTemp = prefs.currentTemperature.toInt())

        if (snapshot.value != 0.0)
            valueField.set(snapshot.value.toString())
        imageUrl.set(snapshot.image)
        dateField.set(formatTimeDate(snapshot.timestamp))
        outsideTemp.set(if (snapshot.outsideTemp != null) snapshot.outsideTemp.toString() else "")
        boilerTemp.set(snapshot.boilerTemp.toString())
    }

    fun onValueChanged(text: CharSequence) {
        L.v("$text")
        valueErrorField.set(null)
    }

    fun onSave(view: DetailsView) {
        if (validateValue()) {
            if (boilerTemp.get().isNotEmpty())
                snapshot.boilerTemp = boilerTemp.get().toInt()

            if (outsideTemp.get().isNotEmpty())
                snapshot.outsideTemp = outsideTemp.get().toInt()
            else
                snapshot.outsideTemp = null

            db.saveSnapshot(snapshot)

            view.finish()
        }
    }

    // todo proper validators
    private fun validateValue(): Boolean = try {
        snapshot.value = valueField.get().toDouble()
        true
    } catch (e: Exception) {
        valueErrorField.set(getString(R.string.invalid_data))
        false
    }

    fun onDatePicked(date: Date) {
        snapshot.timestamp = date.time
        dateField.set(formatTimeDate(date.time))
    }

    fun truncDate(): Date {
        calendar.time = Date(snapshot.timestamp)
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        return calendar.time
    }

    fun onDatePick(view: DetailsView) = view.pickDate()

    class Factory(private val snapshotId: String? = null) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = DetailsViewModel(snapshotId) as T
    }

}
